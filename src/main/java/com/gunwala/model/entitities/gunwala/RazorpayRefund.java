package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "razorpay_refunds")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class RazorpayRefund extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REFUND_PK_ID")
    private int refundPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    // Razorpay's refund ID (e.g., rfnd_ABC123)
    @Column(name = "RAZORPAY_REFUND_ID", unique = true, nullable = false, length = 100)
    private String razorpayRefundId;

    @Column(name = "RAZORPAY_PAYMENT_ID", nullable = false, length = 100)
    private String razorpayPaymentId;

    @Column(name = "PAYMENT_ID", nullable = false)
    private int paymentId;

    @Column(name = "ORDER_ID", nullable = false)
    private int orderId;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

    // Amount refunded in paise
    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "CURRENCY", length = 10, nullable = false)
    private String currency = "INR";

    // PENDING | PROCESSED | FAILED
    @Column(name = "STATUS", length = 30, nullable = false)
    private String status = "PENDING";

    // normal | optimum | instant
    @Column(name = "SPEED_REQUESTED", length = 20)
    private String speedRequested = "normal";

    @Column(name = "SPEED_PROCESSED", length = 20)
    private String speedProcessed;

    @Column(name = "REASON", length = 255)
    private String reason;

    // Who initiated it: CUSTOMER | ADMIN | SYSTEM
    @Column(name = "INITIATED_BY", length = 30)
    private String initiatedBy;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "BATCH_ID", length = 100)
    private String batchId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        versionId  = UUID.randomUUID();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
