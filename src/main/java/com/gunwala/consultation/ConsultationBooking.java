package com.gunwala.consultation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** A user's booked consultation. Topic and user details are copied in so history survives edits. */
@Entity
@Table(name = "consultation_bookings",
        indexes = {
                @Index(name = "idx_consultation_booking_user", columnList = "user_id"),
                @Index(name = "idx_consultation_booking_slot", columnList = "slot_id"),
                @Index(name = "idx_consultation_booking_start", columnList = "starts_at")
        })
@Getter
@Setter
@NoArgsConstructor
public class ConsultationBooking {

    public enum Status { CONFIRMED, CANCELLED }

    public enum CancelledBy { USER, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The slot this booking holds. Cleared if an admin later deletes the slot of a cancelled booking. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id")
    private ConsultationSlot slot;

    /** Copied from the slot so the booking keeps its time even without it. */
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private int durationMinutes;

    private Long topicId;

    @Column(nullable = false, length = 80)
    private String topicName;

    /** The user's nodeId (same id chat and orders use). */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(length = 150)
    private String userName;

    @Column(nullable = false, length = 200)
    private String userEmail;

    @Column(length = 20)
    private String phone;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.CONFIRMED;

    /** Google Meet link; null until the calendar event is created (or an admin adds one). */
    @Column(length = 300)
    private String meetLink;

    /** Google Calendar event id, used to cancel the invite. */
    @Column(length = 200)
    private String calendarEventId;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private CancelledBy cancelledBy;

    @Column(length = 300)
    private String cancelReason;

    private Instant cancelledAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Instant getEndsAt() {
        return startsAt.plusSeconds(durationMinutes * 60L);
    }
}
