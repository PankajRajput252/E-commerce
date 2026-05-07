package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import lombok.Data;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "razorpay_payments")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class RazorpayPayment  extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PAYMENT_PK_ID")
    private int paymentPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    // Razorpay payment ID (e.g., pay_ABC123)
    @Column(name = "RAZORPAY_PAYMENT_ID", unique = true, nullable = false, length = 100)
    private String razorpayPaymentId;

    // Razorpay order ID this payment belongs to
    @Column(name = "RAZORPAY_ORDER_ID", nullable = false, length = 100)
    private String razorpayOrderId;

    // FK to our internal order table
    @Column(name = "ORDER_ID", nullable = false)
    private int orderId;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private int productId;

    // Amount in paise
    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "CURRENCY", length = 10, nullable = false)
    private String currency = "INR";

    // CREATED | AUTHORIZED | CAPTURED | REFUNDED | FAILED
    @Column(name = "STATUS", length = 30, nullable = false)
    private String status;

    // upi | card | netbanking | wallet | emi
    @Column(name = "METHOD", length = 30)
    private String method;

    // For UPI: VPA used
    @Column(name = "VPA", length = 100)
    private String vpa;

    // For Card: masked card number, card network
    @Column(name = "CARD_ID", length = 100)
    private String cardId;

    @Column(name = "CARD_NETWORK", length = 30)
    private String cardNetwork;

    @Column(name = "CARD_ISSUER", length = 100)
    private String cardIssuer;

    // For Wallet/Netbanking: bank name
    @Column(name = "BANK", length = 100)
    private String bank;

    @Column(name = "WALLET", length = 50)
    private String wallet;

    // Razorpay signature (used to verify authenticity)
    @Column(name = "RAZORPAY_SIGNATURE", length = 500)
    private String razorpaySignature;

    // Whether signature verification passed
    @Column(name = "IS_SIGNATURE_VALID")
    private boolean isSignatureValid = false;

    // Invoice / receipt ID from Razorpay if applicable
    @Column(name = "INVOICE_ID", length = 100)
    private String invoiceId;

    // Razorpay error details on failure
    @Column(name = "ERROR_CODE", length = 100)
    private String errorCode;

    @Column(name = "ERROR_DESCRIPTION", columnDefinition = "TEXT")
    private String errorDescription;

    @Column(name = "ERROR_REASON", length = 100)
    private String errorReason;

    @Column(name = "ERROR_SOURCE", length = 100)
    private String errorSource;

    @Column(name = "ERROR_STEP", length = 100)
    private String errorStep;

    // Timestamp when Razorpay captured this payment
    @Column(name = "CAPTURED_AT")
    private LocalDateTime capturedAt;

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
