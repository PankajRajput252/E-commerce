package com.gunwala.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * One in-app notification for one user (the bell in the header). An event for "all admins" is stored
 * once per admin so each keeps their own read state.
 *
 * Shown as:  [avatar] {actorName} {action} · 5 hours ago
 *                     [category icon] {title}
 *                     {reference} • {status}
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_recipient_time", columnList = "recipient_id, created_at"),
        @Index(name = "idx_notification_recipient_read", columnList = "recipient_id, is_read")
})
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    /** DIRECT: about the user's own orders/payments/bookings. ADMIN: store activity sent to every admin. */
    public static final String AUDIENCE_DIRECT = "DIRECT";
    public static final String AUDIENCE_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The recipient's nodeId (same id chat and consultations use). */
    @Column(name = "recipient_id", nullable = false, length = 100)
    private String recipientId;

    @Column(nullable = false, length = 10)
    private String audience = AUDIENCE_DIRECT;

    /** PAYMENT, ORDER, SHIPPING, CONSULTATION, USER — picks the icon. */
    @Column(nullable = false, length = 20)
    private String category;

    /** Who did it; null = the system (shown as "Bandookwale"). */
    @Column(length = 100)
    private String actorId;

    @Column(length = 150)
    private String actorName;

    /** Verb phrase after the actor's name, e.g. "approved your payment". */
    @Column(nullable = false, length = 200)
    private String action;

    /** What it's about, e.g. "₹566 for Consultation #8". */
    @Column(nullable = false, length = 300)
    private String title;

    /** Short id of the thing, e.g. "BW-20261116-AB12" or "Consultation #8". */
    @Column(length = 80)
    private String reference;

    /** Current state of the thing, e.g. "Approved", "Shipped". */
    @Column(length = 40)
    private String status;

    /** Frontend path opened on click. */
    @Column(length = 300)
    private String link;

    /** Updates about the same thing share a key and are grouped ("+3 updates"). */
    @Column(length = 100)
    private String groupKey;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
