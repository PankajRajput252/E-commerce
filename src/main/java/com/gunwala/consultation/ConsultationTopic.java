package com.gunwala.consultation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** Something users can book a consultation about (Business, Guns, Accessories…). Managed by admins. */
@Entity
@Table(name = "consultation_topics")
@Getter
@Setter
@NoArgsConstructor
public class ConsultationTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 300)
    private String description;

    /** Hidden from users when false; existing bookings keep their topic name. */
    @Column(nullable = false)
    private boolean active = true;

    /** Lower numbers are shown first. */
    @Column(nullable = false)
    private int sortOrder;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
