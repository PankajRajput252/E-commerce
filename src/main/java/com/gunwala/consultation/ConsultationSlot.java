package com.gunwala.consultation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** A time an admin is available for one consultation. Booked by at most one confirmed booking. */
@Entity
@Table(name = "consultation_slots",
        uniqueConstraints = @UniqueConstraint(name = "uk_consultation_slot_start", columnNames = "starts_at"),
        indexes = @Index(name = "idx_consultation_slot_start", columnList = "starts_at"))
@Getter
@Setter
@NoArgsConstructor
public class ConsultationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private int durationMinutes;

    /** True while a CONFIRMED booking holds this slot; cancelling frees it again. */
    @Column(nullable = false)
    private boolean booked;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Instant getEndsAt() {
        return startsAt.plusSeconds(durationMinutes * 60L);
    }
}
