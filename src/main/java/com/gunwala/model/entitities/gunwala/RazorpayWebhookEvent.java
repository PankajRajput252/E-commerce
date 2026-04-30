package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "razorpay_webhook_events")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class RazorpayWebhookEvent extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EVENT_PK_ID")
    private int eventPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    // Razorpay event ID for idempotency
    @Column(name = "RAZORPAY_EVENT_ID", unique = true, nullable = false, length = 100)
    private String razorpayEventId;

    // e.g. payment.captured, payment.failed, refund.created, order.paid
    @Column(name = "EVENT_TYPE", nullable = false, length = 100)
    private String eventType;

    // Full raw JSON payload from Razorpay
    @Column(name = "PAYLOAD", columnDefinition = "LONGTEXT", nullable = false)
    private String payload;

    // RECEIVED | PROCESSED | FAILED | DUPLICATE
    @Column(name = "STATUS", length = 30, nullable = false)
    private String status = "RECEIVED";

    // Razorpay-Signature header value
    @Column(name = "WEBHOOK_SIGNATURE", length = 500)
    private String webhookSignature;

    @Column(name = "IS_SIGNATURE_VALID")
    private boolean isSignatureValid = false;

    // Error message if processing failed
    @Column(name = "ERROR_MESSAGE", columnDefinition = "TEXT")
    private String errorMessage;

    // Number of retry attempts
    @Column(name = "RETRY_COUNT")
    private int retryCount = 0;

    // Razorpay's event timestamp (from payload)
    @Column(name = "EVENT_TIMESTAMP")
    private LocalDateTime eventTimestamp;

    @Column(name = "RECEIVED_AT")
    private LocalDateTime receivedAt;

    @Column(name = "PROCESSED_AT")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
        versionId  = UUID.randomUUID();
    }
}
