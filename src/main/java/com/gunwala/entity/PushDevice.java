package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * A phone (app install) that can receive push notifications for a user.
 * The FCM token identifies the install; one user can have several devices.
 */
@Entity
@Table(name = "push_devices",
        uniqueConstraints = @UniqueConstraint(name = "uk_push_device_token", columnNames = "token"),
        indexes = @Index(name = "idx_push_device_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class PushDevice {

    public enum Platform { ANDROID, IOS, WEB }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FCM registration token from the app (Firebase Messaging getToken()). */
    @Column(nullable = false, length = 512)
    private String token;

    /** The user's nodeId — the same id chat uses for senders and recipients. */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Platform platform;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Last time the app registered this token; refreshed on every app start / login. */
    @Column(nullable = false)
    private Instant lastSeenAt;

    @PrePersist
    void onCreate() {
        createdAt = lastSeenAt = Instant.now();
    }
}
