package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A payment the end user made (outside the app) and reported with a receipt.
 * Lifecycle: PENDING -> APPROVED | REJECTED (both final).
 */
@Entity
@Table(name = "payment_submissions", indexes = {
        @Index(name = "idx_ps_user", columnList = "user_id"),
        @Index(name = "idx_ps_status", columnList = "status"),
        @Index(name = "idx_ps_txn_ref", columnList = "transaction_reference")
})
@Getter
@Setter
@NoArgsConstructor
public class PaymentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** UTR / UPI reference / bank transaction id entered by the user. */
    @Column(name = "transaction_reference", nullable = false, length = 50)
    private String transactionReference;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(length = 500)
    private String userRemarks;

    /** The shop order this payment is for (ShopOrder.orderReference), if any. */
    @Column(name = "order_reference", length = 50)
    private String orderReference;

    // ---- Receipt file ----
    @Column(nullable = false, length = 255)
    private String receiptPath;

    @Column(length = 255)
    private String receiptOriginalName;

    @Column(nullable = false, length = 50)
    private String receiptContentType;

    // ---- Review ----
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;

    @Column(length = 100)
    private String reviewedBy;

    private Instant reviewedAt;

    /** Optimistic lock: stops two admins approving/rejecting the same payment at once. */
    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
