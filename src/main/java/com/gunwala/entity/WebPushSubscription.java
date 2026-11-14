package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * A browser that can receive Web Push notifications for a user (standard Web Push + VAPID,
 * no Firebase). The endpoint is the browser vendor's push URL; p256dh/auth are the browser's
 * keys used to encrypt the payload so only that browser can read it.
 */
@Entity
@Table(name = "web_push_subscriptions",
        uniqueConstraints = @UniqueConstraint(name = "uk_web_push_endpoint", columnNames = "endpoint"),
        indexes = @Index(name = "idx_web_push_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class WebPushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Push service URL (e.g. https://fcm.googleapis.com/fcm/send/..., https://updates.push.services.mozilla.com/...). */
    @Column(nullable = false, length = 700)
    private String endpoint;

    /** Browser's P-256 public key, base64url (65 bytes uncompressed). */
    @Column(nullable = false, length = 128)
    private String p256dh;

    /** Browser's 16-byte auth secret, base64url. */
    @Column(nullable = false, length = 64)
    private String auth;

    /** The user's nodeId — the same id chat uses for senders and recipients. */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Last time the site re-registered this subscription (every page load while logged in). */
    @Column(nullable = false)
    private Instant lastSeenAt;

    @PrePersist
    void onCreate() {
        createdAt = lastSeenAt = Instant.now();
    }
}
