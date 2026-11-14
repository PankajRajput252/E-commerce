package com.gunwala.consultation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates Google Calendar events with a Google Meet link for each booking, using the Google account
 * an admin connected once (OAuth "offline" access → refresh token stored in the database).
 *
 * Talks to Google's REST endpoints with the JDK HTTP client, so there are no extra dependencies.
 *
 * Server settings: GOOGLE_OAUTH_CLIENT_ID, GOOGLE_OAUTH_CLIENT_SECRET, GOOGLE_OAUTH_REDIRECT_URI
 * (the redirect URI must be …/api/consultations/google/callback and be listed in the Google Cloud console).
 */
@Slf4j
@Service
public class GoogleCalendarService {

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final String SCOPES = "openid email https://www.googleapis.com/auth/calendar.events";
    private static final Duration STATE_LIFETIME = Duration.ofMinutes(15);

    private final GoogleCalendarAccountRepository accountRepository;
    private final ObjectMapper json;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final ZoneId zone;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private String cachedAccessToken;
    private Instant cachedAccessTokenExpiry = Instant.EPOCH;

    public GoogleCalendarService(GoogleCalendarAccountRepository accountRepository,
                                 ObjectMapper json,
                                 @Value("${google.oauth.client-id:}") String clientId,
                                 @Value("${google.oauth.client-secret:}") String clientSecret,
                                 @Value("${google.oauth.redirect-uri:}") String redirectUri,
                                 @Value("${consultation.timezone:Asia/Kolkata}") String timezone) {
        this.accountRepository = accountRepository;
        this.json = json;
        this.clientId = clientId.trim();
        this.clientSecret = clientSecret.trim();
        this.redirectUri = redirectUri.trim();
        this.zone = ZoneId.of(timezone);
        if (!isConfigured()) {
            log.warn("Consultation Meet links are OFF: set GOOGLE_OAUTH_CLIENT_ID, GOOGLE_OAUTH_CLIENT_SECRET and GOOGLE_OAUTH_REDIRECT_URI");
        }
    }

    /** OAuth client is set on the server. */
    public boolean isConfigured() {
        return !clientId.isEmpty() && !clientSecret.isEmpty() && !redirectUri.isEmpty();
    }

    /** An admin has linked the Google account that hosts the calls. */
    public boolean isConnected() {
        return isConfigured() && accountRepository.existsById(GoogleCalendarAccount.SINGLETON_ID);
    }

    public Optional<GoogleCalendarAccount> account() {
        return accountRepository.findById(GoogleCalendarAccount.SINGLETON_ID);
    }

    // ============================================================ connect / disconnect

    /** Google consent page URL. prompt=consent makes Google always return a refresh token. */
    public String authorizeUrl() {
        requireConfigured();
        return AUTH_URL + "?" + form(Map.of(
                "client_id", clientId,
                "redirect_uri", redirectUri,
                "response_type", "code",
                "scope", SCOPES,
                "access_type", "offline",
                "prompt", "consent",
                "include_granted_scopes", "true",
                "state", newState()));
    }

    /** Google redirected back with ?code=…&state=…: exchange the code and store the refresh token. */
    public synchronized GoogleCalendarAccount completeAuthorization(String code, String state) {
        requireConfigured();
        if (!isValidState(state)) throw new IllegalArgumentException("The sign-in link expired. Please try connecting again.");

        JsonNode token = postForm(TOKEN_URL, Map.of(
                "code", code,
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "grant_type", "authorization_code"));
        String refreshToken = token.path("refresh_token").asText(null);
        if (!StringUtils.hasText(refreshToken)) {
            throw new IllegalStateException("Google did not return a refresh token. Remove Bandookwale's access at "
                    + "myaccount.google.com/permissions and connect again.");
        }
        String scope = token.path("scope").asText("");
        if (!scope.contains("calendar.events") && !scope.contains("auth/calendar")) {
            throw new IllegalStateException("Calendar permission was not granted. Please connect again and allow calendar access.");
        }

        GoogleCalendarAccount account = account().orElseGet(GoogleCalendarAccount::new);
        account.setRefreshToken(refreshToken);
        account.setAccountEmail(emailFromIdToken(token.path("id_token").asText(null)));
        account.setConnectedAt(Instant.now());
        accountRepository.save(account);

        cachedAccessToken = token.path("access_token").asText(null);
        cachedAccessTokenExpiry = Instant.now().plusSeconds(token.path("expires_in").asLong(0) - 60);
        log.info("Consultation Google account connected: {}", account.getAccountEmail());
        return account;
    }

    public synchronized void disconnect() {
        account().ifPresent(a -> {
            try {
                // Revoke so the token stops working even if a copy of the database leaks later
                http.send(HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/revoke?token="
                                + URLEncoder.encode(a.getRefreshToken(), StandardCharsets.UTF_8)))
                        .timeout(Duration.ofSeconds(15))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build(), HttpResponse.BodyHandlers.discarding());
            } catch (Exception e) {
                log.warn("Could not revoke Google token: {}", e.getMessage());
            }
            accountRepository.delete(a);
        });
        cachedAccessToken = null;
        cachedAccessTokenExpiry = Instant.EPOCH;
    }

    // ============================================================ events

    public record CreatedEvent(String eventId, String meetLink) {}

    /**
     * Creates the calendar event with a Meet link and invites the user (Google emails them the invite).
     * Throws if Google isn't connected or the call fails; the booking stays valid either way.
     */
    public CreatedEvent createMeeting(ConsultationBooking booking) {
        DateTimeFormatter iso = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("summary", "Bandookwale consultation: " + booking.getTopicName()
                + (StringUtils.hasText(booking.getUserName()) ? " with " + booking.getUserName() : ""));
        body.put("description", eventDescription(booking));
        body.put("start", Map.of("dateTime", iso.format(booking.getStartsAt().atZone(zone)), "timeZone", zone.getId()));
        body.put("end", Map.of("dateTime", iso.format(booking.getEndsAt().atZone(zone)), "timeZone", zone.getId()));
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("email", booking.getUserEmail());
        if (StringUtils.hasText(booking.getUserName())) attendee.put("displayName", booking.getUserName());
        body.put("attendees", List.of(attendee));
        body.put("guestsCanSeeOtherGuests", false);
        body.put("guestsCanInviteOthers", false);
        body.put("reminders", Map.of("useDefault", true));
        body.put("conferenceData", Map.of("createRequest", Map.of(
                "requestId", "bw-consultation-" + booking.getId() + "-" + UUID.randomUUID(),
                "conferenceSolutionKey", Map.of("type", "hangoutsMeet"))));

        JsonNode event = authorized("POST", EVENTS_URL + "?conferenceDataVersion=1&sendUpdates=all", body);
        String eventId = event.path("id").asText(null);
        String link = meetLinkOf(event);

        // Meet creation is usually instant, but Google may report it as "pending" for a moment
        for (int attempt = 0; link == null && eventId != null && attempt < 4; attempt++) {
            sleep(1000);
            link = meetLinkOf(authorized("GET", EVENTS_URL + "/" + enc(eventId) + "?conferenceDataVersion=1", null));
        }
        if (link == null) throw new IllegalStateException("Google created the event but no Meet link yet");
        return new CreatedEvent(eventId, link);
    }

    /** Deletes the event; Google emails the attendee that it was cancelled. Missing events are ignored. */
    public void cancelMeeting(String eventId) {
        if (!StringUtils.hasText(eventId)) return;
        try {
            authorized("DELETE", EVENTS_URL + "/" + enc(eventId) + "?sendUpdates=all", null);
        } catch (GoogleApiException e) {
            if (e.status != 404 && e.status != 410) throw e;
        }
    }

    // ============================================================ HTTP

    static final class GoogleApiException extends RuntimeException {
        final int status;

        GoogleApiException(int status, String message) {
            super(message);
            this.status = status;
        }
    }

    private JsonNode authorized(String method, String url, Object body) {
        String token = accessToken();
        try {
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json");
            if (body != null) {
                req.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofByteArray(json.writeValueAsBytes(body)));
            } else {
                req.method(method, HttpRequest.BodyPublishers.noBody());
            }
            HttpResponse<String> res = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 401) {
                // Access token revoked early; drop it so the next call refreshes
                synchronized (this) {
                    cachedAccessToken = null;
                }
            }
            if (res.statusCode() >= 400) {
                throw new GoogleApiException(res.statusCode(), "Google Calendar error " + res.statusCode() + ": " + errorText(res.body()));
            }
            return res.body() == null || res.body().isBlank() ? json.createObjectNode() : json.readTree(res.body());
        } catch (GoogleApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling Google");
        } catch (Exception e) {
            throw new IllegalStateException("Could not reach Google Calendar: " + e.getMessage(), e);
        }
    }

    private synchronized String accessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedAccessTokenExpiry)) return cachedAccessToken;
        requireConfigured();
        GoogleCalendarAccount account = account()
                .orElseThrow(() -> new IllegalStateException("No Google account connected for consultations"));
        try {
            JsonNode token = postForm(TOKEN_URL, Map.of(
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "refresh_token", account.getRefreshToken(),
                    "grant_type", "refresh_token"));
            cachedAccessToken = token.path("access_token").asText();
            cachedAccessTokenExpiry = Instant.now().plusSeconds(token.path("expires_in").asLong(3600) - 60);
            return cachedAccessToken;
        } catch (GoogleApiException e) {
            if (e.getMessage().contains("invalid_grant")) {
                // Access was revoked, the password changed, or (OAuth app in "Testing") the 7-day token expired
                log.error("Google refused the stored refresh token; consultations are disconnected. Reconnect in the admin panel.");
                accountRepository.delete(account);
            }
            throw e;
        }
    }

    private JsonNode postForm(String url, Map<String, String> fields) {
        try {
            HttpResponse<String> res = http.send(HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form(fields)))
                    .build(), HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 400) {
                throw new GoogleApiException(res.statusCode(), "Google sign-in error " + res.statusCode() + ": " + errorText(res.body()));
            }
            return json.readTree(res.body());
        } catch (GoogleApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling Google");
        } catch (Exception e) {
            throw new IllegalStateException("Could not reach Google: " + e.getMessage(), e);
        }
    }

    // ============================================================ helpers

    private void requireConfigured() {
        if (!isConfigured()) throw new IllegalStateException("Google OAuth is not configured on the server");
    }

    /** state = expiry.signature, so it can't be forged and works on any server instance. */
    private String newState() {
        String expiry = String.valueOf(Instant.now().plus(STATE_LIFETIME).getEpochSecond());
        return expiry + "." + sign(expiry);
    }

    private boolean isValidState(String state) {
        if (state == null) return false;
        int dot = state.indexOf('.');
        if (dot <= 0) return false;
        String expiry = state.substring(0, dot);
        byte[] expected = sign(expiry).getBytes(StandardCharsets.US_ASCII);
        byte[] actual = state.substring(dot + 1).getBytes(StandardCharsets.US_ASCII);
        try {
            return MessageDigest.isEqual(expected, actual) && Instant.now().getEpochSecond() < Long.parseLong(expiry);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(("consultation-oauth-state:" + clientSecret).getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String emailFromIdToken(String idToken) {
        if (!StringUtils.hasText(idToken)) return null;
        try {
            // Came straight from Google's token endpoint over TLS, so reading the payload is enough here
            String payload = idToken.split("\\.")[1];
            return json.readTree(Base64.getUrlDecoder().decode(payload)).path("email").asText(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static String meetLinkOf(JsonNode event) {
        String link = event.path("hangoutLink").asText(null);
        if (StringUtils.hasText(link)) return link;
        for (JsonNode ep : event.path("conferenceData").path("entryPoints")) {
            if ("video".equals(ep.path("entryPointType").asText()) && StringUtils.hasText(ep.path("uri").asText(null))) {
                return ep.path("uri").asText();
            }
        }
        return null;
    }

    private String eventDescription(ConsultationBooking b) {
        StringBuilder sb = new StringBuilder("Consultation booked on Bandookwale.\n\nTopic: ").append(b.getTopicName());
        if (StringUtils.hasText(b.getUserName())) sb.append("\nName: ").append(b.getUserName());
        sb.append("\nEmail: ").append(b.getUserEmail());
        if (StringUtils.hasText(b.getPhone())) sb.append("\nPhone: ").append(b.getPhone());
        if (StringUtils.hasText(b.getNotes())) sb.append("\n\nWhat they'd like to discuss:\n").append(b.getNotes());
        return sb.toString();
    }

    private String errorText(String body) {
        try {
            JsonNode n = json.readTree(body);
            if (n.path("error").isTextual()) {
                return n.path("error").asText() + (n.has("error_description") ? " (" + n.path("error_description").asText() + ")" : "");
            }
            return n.path("error").path("message").asText(body);
        } catch (Exception e) {
            return body == null ? "" : body.substring(0, Math.min(body.length(), 300));
        }
    }

    private static String form(Map<String, String> fields) {
        return fields.entrySet().stream()
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
