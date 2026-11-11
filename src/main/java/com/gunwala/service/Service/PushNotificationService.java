package com.gunwala.service.Service;

import com.google.firebase.messaging.*;
import com.gunwala.entity.PushDevice;
import com.gunwala.repository.PushDeviceRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Push notifications to the mobile app through Firebase Cloud Messaging (FCM).
 * FCM delivers even when the app is closed / not running in the background:
 * Android shows the "notification" part itself, iOS gets it through APNs.
 *
 * Sending happens on a background thread so a slow or failing Firebase call
 * never delays the chat message itself.
 */
@Slf4j
@Service
public class PushNotificationService {

    /** Android notification channel the app must create (see the app setup notes). */
    public static final String CHAT_CHANNEL_ID = "chat_messages";

    private static final int MAX_PREVIEW = 120;
    /** Errors meaning the token is dead (app uninstalled, token rotated, other Firebase project). */
    private static final Set<MessagingErrorCode> DEAD_TOKEN_ERRORS = EnumSet.of(
            MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT,
            MessagingErrorCode.SENDER_ID_MISMATCH);

    private final PushDeviceRepository deviceRepository;
    private final FirebaseMessaging messaging; // null when Firebase isn't configured
    private final ExecutorService sender = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "push-sender");
        t.setDaemon(true);
        return t;
    });

    public PushNotificationService(PushDeviceRepository deviceRepository,
                                   Optional<FirebaseMessaging> firebaseMessaging) {
        this.deviceRepository = deviceRepository;
        this.messaging = firebaseMessaging.orElse(null);
    }

    @PreDestroy
    void shutdown() {
        sender.shutdown();
    }

    // ============================================================ devices

    /** Saves (or moves) a device token to this user. Called on every app start / login. */
    @Transactional
    public void registerDevice(String userId, String token, PushDevice.Platform platform) {
        PushDevice device = deviceRepository.findByToken(token).orElseGet(PushDevice::new);
        device.setToken(token);
        device.setUserId(userId);   // a token re-registered after another login now belongs to the new user
        device.setPlatform(platform);
        device.setLastSeenAt(Instant.now());
        deviceRepository.save(device);
    }

    /** Called by the app on logout, so that phone stops getting this user's chats. */
    @Transactional
    public void unregisterDevice(String token) {
        deviceRepository.deleteByToken(token);
    }

    // ============================================================ chat

    /**
     * New marketplace chat message for {@code recipientId}. The data payload lets the app
     * open the right conversation when the notification is tapped.
     *
     * @param unreadTotal recipient's total unread messages, used as the app icon badge
     */
    public void notifyChatMessage(String recipientId, String senderId, String senderName,
                                  String content, String conversationId, String productTitle,
                                  int unreadTotal) {
        if (messaging == null || !StringUtils.hasText(recipientId)) return;

        String title = StringUtils.hasText(senderName) ? senderName : "New message";
        String body = preview(content);
        if (StringUtils.hasText(productTitle)) {
            body = body + "  ·  " + productTitle;
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", "CHAT_MESSAGE");
        data.put("conversationId", nullToEmpty(conversationId));
        data.put("senderId", nullToEmpty(senderId));
        data.put("senderName", nullToEmpty(senderName));
        data.put("productTitle", nullToEmpty(productTitle));
        data.put("unreadTotal", String.valueOf(unreadTotal));

        String finalBody = body;
        sender.execute(() -> send(recipientId, title, finalBody, data, conversationId, unreadTotal));
    }

    // ============================================================ sending

    private void send(String userId, String title, String body, Map<String, String> data,
                      String groupKey, int badge) {
        try {
            List<String> tokens = deviceRepository.findByUserId(userId).stream()
                    .map(PushDevice::getToken)
                    .distinct()
                    .toList();
            if (tokens.isEmpty()) return;

            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            // HIGH wakes the phone so it shows right away, even in Doze
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId(CHAT_CHANNEL_ID)
                                    // Same tag = one notification per conversation, updated instead of stacked
                                    .setTag(groupKey)
                                    .setSound("default")
                                    .setNotificationCount(badge)
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .putHeader("apns-priority", "10")
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(badge)
                                    .setThreadId(groupKey) // groups a conversation's notifications on iOS
                                    .build())
                            .build())
                    .build();

            BatchResponse result = messaging.sendEachForMulticast(message);
            pruneDeadTokens(tokens, result);
            if (result.getFailureCount() > 0) {
                log.debug("Push to {}: {} sent, {} failed", userId, result.getSuccessCount(), result.getFailureCount());
            }
        } catch (Exception e) {
            // Never let a push failure affect chat
            log.warn("Push notification to {} failed: {}", userId, e.getMessage());
        }
    }

    private void pruneDeadTokens(List<String> tokens, BatchResponse result) {
        List<String> dead = new ArrayList<>();
        List<SendResponse> responses = result.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            FirebaseMessagingException error = responses.get(i).getException();
            if (error != null && DEAD_TOKEN_ERRORS.contains(error.getMessagingErrorCode())) {
                dead.add(tokens.get(i));
            }
        }
        if (!dead.isEmpty()) {
            deviceRepository.deleteByTokenIn(dead);
            log.info("Removed {} expired push token(s)", dead.size());
        }
    }

    private static String preview(String content) {
        if (!StringUtils.hasText(content)) return "Sent you a message";
        String oneLine = content.replaceAll("\\s+", " ").trim();
        return oneLine.length() > MAX_PREVIEW ? oneLine.substring(0, MAX_PREVIEW - 1) + "…" : oneLine;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
