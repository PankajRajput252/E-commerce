package com.gunwala.service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunwala.entity.WebPushSubscription;
import com.gunwala.exception.BadRequestException;
import com.gunwala.repository.WebPushSubscriptionRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Browser push notifications (standard Web Push, no Firebase): the website gets chat alerts
 * even when no Bandookwale tab is open, as long as the browser is running.
 *
 * Implements the two standards involved using only the JDK:
 *   - VAPID (RFC 8292): an ES256-signed JWT proving the push comes from this server
 *   - Message encryption (RFC 8291, aes128gcm): only the subscribed browser can read the payload
 *
 * Keys: set VAPID_PUBLIC_KEY and VAPID_PRIVATE_KEY (base64url). Without them the backend runs
 * normally and web push is simply off.
 */
@Slf4j
@Service
public class WebPushService {

    private static final int MAX_PREVIEW = 120;
    private static final int RECORD_SIZE = 4096;
    private static final long JWT_LIFETIME_SECONDS = 12 * 60 * 60; // the spec allows at most 24h
    private static final ECParameterSpec P256 = p256();

    private final WebPushSubscriptionRepository repository;
    private final ObjectMapper json;
    private final String subject;
    private final String publicKeyB64;      // null when web push is off
    private final byte[] publicKey;
    private final PrivateKey privateKey;

    private final SecureRandom random = new SecureRandom();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final Map<String, CachedJwt> jwtByAudience = new ConcurrentHashMap<>();
    private final ExecutorService sender = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "web-push-sender");
        t.setDaemon(true);
        return t;
    });

    private record CachedJwt(String token, long renewAfterEpochSecond) {}

    public WebPushService(WebPushSubscriptionRepository repository,
                          ObjectMapper json,
                          @Value("${webpush.public-key:}") String publicKey,
                          @Value("${webpush.private-key:}") String privateKey,
                          @Value("${webpush.subject:https://bandookwale.com}") String subject) {
        this.repository = repository;
        this.json = json;
        this.subject = subject;

        String pubB64 = null;
        byte[] pub = null;
        PrivateKey priv = null;
        if (!StringUtils.hasText(publicKey) || !StringUtils.hasText(privateKey)) {
            log.warn("Web push notifications are OFF: set VAPID_PUBLIC_KEY and VAPID_PRIVATE_KEY");
        } else {
            try {
                pub = b64urlDecode(publicKey);
                ECPublicKey ecPub = decodePublicKey(pub);
                priv = KeyFactory.getInstance("EC").generatePrivate(
                        new ECPrivateKeySpec(new BigInteger(1, b64urlDecode(privateKey)), P256));
                checkKeyPair(priv, ecPub);
                pubB64 = b64url(pub);
                log.info("Web push notifications are ON");
            } catch (Exception e) {
                // Bad keys must not stop the whole backend from starting
                log.error("Web push notifications are OFF: VAPID keys are invalid or don't match ({})", e.getMessage());
                pub = null;
                priv = null;
            }
        }
        this.publicKeyB64 = pubB64;
        this.publicKey = pub;
        this.privateKey = priv;
    }

    @PreDestroy
    void shutdown() {
        sender.shutdown();
    }

    public boolean isEnabled() {
        return publicKeyB64 != null;
    }

    /** VAPID public key the browser needs for pushManager.subscribe(). */
    public String getPublicKey() {
        return publicKeyB64;
    }

    // ============================================================ subscriptions

    /** Saves (or moves) a browser subscription to this user. Called on every page load while logged in. */
    @Transactional
    public void subscribe(String userId, String endpoint, String p256dh, String auth) {
        URI uri;
        try {
            uri = URI.create(endpoint);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid push endpoint");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new BadRequestException("Push endpoint must be an https URL");
        }
        try {
            decodePublicKey(b64urlDecode(p256dh));
            if (b64urlDecode(auth).length != 16) throw new IllegalArgumentException("auth must be 16 bytes");
        } catch (Exception e) {
            throw new BadRequestException("Invalid push subscription keys");
        }

        WebPushSubscription sub = repository.findByEndpoint(endpoint).orElseGet(WebPushSubscription::new);
        sub.setEndpoint(endpoint);
        sub.setP256dh(p256dh);
        sub.setAuth(auth);
        sub.setUserId(userId);   // a browser re-registered after another login now belongs to the new user
        sub.setLastSeenAt(Instant.now());
        repository.save(sub);
    }

    /** Called by the website on logout, so this browser stops getting the user's chats. */
    @Transactional
    public void unsubscribe(String userId, String endpoint) {
        repository.findByEndpoint(endpoint)
                .filter(s -> s.getUserId().equals(userId))
                .ifPresent(repository::delete);
    }

    // ============================================================ chat

    /** New chat message for {@code recipientId}: notify all their subscribed browsers. */
    public void notifyChatMessage(String recipientId, String senderName, String content,
                                  String conversationId, String productTitle) {
        if (!isEnabled() || !StringUtils.hasText(recipientId)) return;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "CHAT_MESSAGE");
        payload.put("title", "New message from " + (StringUtils.hasText(senderName) ? senderName : "a user"));
        payload.put("body", preview(content) + (StringUtils.hasText(productTitle) ? "\nRe: " + productTitle : ""));
        payload.put("tag", conversationId);
        payload.put("url", "/chat?c=" + URLEncoder.encode(String.valueOf(conversationId), StandardCharsets.UTF_8));
        payload.put("conversationId", conversationId);

        sender.execute(() -> sendToUser(recipientId, payload));
    }

    // ============================================================ sending

    private void sendToUser(String userId, Map<String, Object> payload) {
        try {
            List<WebPushSubscription> subs = repository.findByUserId(userId);
            if (subs.isEmpty()) return;
            byte[] plaintext = json.writeValueAsBytes(payload);
            for (WebPushSubscription sub : subs) {
                send(sub, plaintext);
            }
        } catch (Exception e) {
            // Never let a push failure affect chat
            log.warn("Web push to {} failed: {}", userId, e.getMessage());
        }
    }

    private void send(WebPushSubscription sub, byte[] plaintext) {
        try {
            URI endpoint = URI.create(sub.getEndpoint());
            byte[] body = encrypt(plaintext, b64urlDecode(sub.getP256dh()), b64urlDecode(sub.getAuth()));

            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(Duration.ofSeconds(30))
                    .header("TTL", "86400")              // keep for a day if the browser is offline
                    .header("Urgency", "high")
                    .header("Content-Encoding", "aes128gcm")
                    .header("Content-Type", "application/octet-stream")
                    .header("Authorization", "vapid t=" + vapidJwt(audienceOf(endpoint)) + ", k=" + publicKeyB64)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status == 404 || status == 410) {
                // Subscription expired or the user revoked permission
                repository.deleteByEndpoint(sub.getEndpoint());
                log.info("Removed expired web push subscription for {}", sub.getUserId());
            } else if (status >= 400) {
                log.warn("Web push rejected ({}): {}", status, response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.warn("Web push send failed: {}", e.getMessage());
        }
    }

    // ============================================================ RFC 8291 encryption

    private byte[] encrypt(byte[] plaintext, byte[] uaPublic, byte[] authSecret) throws GeneralSecurityException {
        // Fresh server key pair for every message
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"), random);
        KeyPair asKeys = kpg.generateKeyPair();
        byte[] asPublic = encodePoint((ECPublicKey) asKeys.getPublic());

        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
        ecdh.init(asKeys.getPrivate());
        ecdh.doPhase(decodePublicKey(uaPublic), true);
        byte[] sharedSecret = ecdh.generateSecret();

        // IKM = HKDF(auth_secret, ecdh_secret, "WebPush: info" || 0x00 || ua_public || as_public, 32)
        byte[] prkKey = hmac(authSecret, sharedSecret);
        byte[] keyInfo = concat("WebPush: info".getBytes(StandardCharsets.US_ASCII), new byte[]{0}, uaPublic, asPublic);
        byte[] ikm = hmac(prkKey, concat(keyInfo, new byte[]{1}));

        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] prk = hmac(salt, ikm);
        byte[] cek = Arrays.copyOf(hmac(prk, concat("Content-Encoding: aes128gcm".getBytes(StandardCharsets.US_ASCII), new byte[]{0, 1})), 16);
        byte[] nonce = Arrays.copyOf(hmac(prk, concat("Content-Encoding: nonce".getBytes(StandardCharsets.US_ASCII), new byte[]{0, 1})), 12);

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cek, "AES"), new GCMParameterSpec(128, nonce));
        byte[] ciphertext = aes.doFinal(concat(plaintext, new byte[]{2})); // 0x02 = last (only) record
        if (ciphertext.length > RECORD_SIZE) throw new IllegalStateException("push payload too large");

        // Header: salt(16) | record size(4) | key id length(1) | key id = as_public(65)
        return ByteBuffer.allocate(16 + 4 + 1 + asPublic.length + ciphertext.length)
                .put(salt)
                .putInt(RECORD_SIZE)
                .put((byte) asPublic.length)
                .put(asPublic)
                .put(ciphertext)
                .array();
    }

    // ============================================================ RFC 8292 VAPID

    private String vapidJwt(String audience) throws Exception {
        long now = Instant.now().getEpochSecond();
        CachedJwt cached = jwtByAudience.get(audience);
        if (cached != null && now < cached.renewAfterEpochSecond()) return cached.token();

        long exp = now + JWT_LIFETIME_SECONDS;
        String header = b64url(json.writeValueAsBytes(Map.of("typ", "JWT", "alg", "ES256")));
        String claims = b64url(json.writeValueAsBytes(Map.of("aud", audience, "exp", exp, "sub", subject)));
        String signingInput = header + "." + claims;

        Signature es256 = Signature.getInstance("SHA256withECDSAinP1363Format"); // raw r||s, as JWS requires
        es256.initSign(privateKey);
        es256.update(signingInput.getBytes(StandardCharsets.US_ASCII));
        String token = signingInput + "." + b64url(es256.sign());

        jwtByAudience.put(audience, new CachedJwt(token, exp - 60 * 60)); // renew an hour before expiry
        return token;
    }

    private static String audienceOf(URI endpoint) {
        return endpoint.getScheme() + "://" + endpoint.getHost() + (endpoint.getPort() == -1 ? "" : ":" + endpoint.getPort());
    }

    // ============================================================ helpers

    private void checkKeyPair(PrivateKey priv, PublicKey pub) throws GeneralSecurityException {
        byte[] probe = "vapid-key-check".getBytes(StandardCharsets.US_ASCII);
        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initSign(priv);
        s.update(probe);
        byte[] sig = s.sign();
        s.initVerify(pub);
        s.update(probe);
        if (!s.verify(sig)) throw new GeneralSecurityException("public key does not match private key");
    }

    private static ECParameterSpec p256() {
        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
            params.init(new ECGenParameterSpec("secp256r1"));
            return params.getParameterSpec(ECParameterSpec.class);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("P-256 not available", e);
        }
    }

    private static ECPublicKey decodePublicKey(byte[] uncompressed) throws GeneralSecurityException {
        if (uncompressed.length != 65 || uncompressed[0] != 0x04) {
            throw new InvalidKeyException("expected a 65-byte uncompressed P-256 point");
        }
        BigInteger x = new BigInteger(1, Arrays.copyOfRange(uncompressed, 1, 33));
        BigInteger y = new BigInteger(1, Arrays.copyOfRange(uncompressed, 33, 65));
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(new ECPoint(x, y), P256));
    }

    private static byte[] encodePoint(ECPublicKey key) {
        return concat(new byte[]{0x04}, unsigned32(key.getW().getAffineX()), unsigned32(key.getW().getAffineY()));
    }

    private static byte[] unsigned32(BigInteger n) {
        byte[] raw = n.toByteArray();
        byte[] out = new byte[32];
        int len = Math.min(raw.length, 32);
        System.arraycopy(raw, raw.length - len, out, 32 - len, len);
        return out;
    }

    private static byte[] hmac(byte[] key, byte[] data) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] p : parts) len += p.length;
        ByteBuffer buf = ByteBuffer.allocate(len);
        for (byte[] p : parts) buf.put(p);
        return buf.array();
    }

    private static String b64url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Accepts base64url or standard base64, with or without padding. */
    private static byte[] b64urlDecode(String s) {
        String normalized = s.trim().replace('+', '-').replace('/', '_').replace("=", "");
        return Base64.getUrlDecoder().decode(normalized);
    }

    private static String preview(String content) {
        if (!StringUtils.hasText(content)) return "Sent you a message";
        String oneLine = content.replaceAll("\\s+", " ").trim();
        return oneLine.length() > MAX_PREVIEW ? oneLine.substring(0, MAX_PREVIEW - 1) + "…" : oneLine;
    }
}
