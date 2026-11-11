package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * One row per browser per day (IST). visitorId is a random id the browser keeps in localStorage,
 * so guests are counted too. visits = browser sessions that day.
 */
@Entity
@Table(name = "site_visits",
        uniqueConstraints = @UniqueConstraint(name = "uk_site_visit_day", columnNames = {"visitor_id", "visit_date"}),
        indexes = @Index(name = "idx_site_visit_date", columnList = "visit_date"))
@Getter
@Setter
@NoArgsConstructor
public class SiteVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false, length = 64)
    private String visitorId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    /** nodeId when the visitor was logged in during any visit that day. */
    @Column(length = 100)
    private String userId;

    @Column(nullable = false)
    private int visits;

    @Column(nullable = false)
    private Instant firstSeenAt;

    @Column(nullable = false)
    private Instant lastSeenAt;
}
