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
@Table(name = "razorpay_orders")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class RazorpayOrder extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_PK_ID")
    private int orderPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    // Razorpay's own order ID (e.g., order_ABC123)
    @Column(name = "RAZORPAY_ORDER_ID", unique = true, nullable = false, length = 100)
    private String razorpayOrderId;

    // Internal reference: user who placed the order
    @Column(name = "USER_ID", nullable = false)
    private int userId;

    // Internal reference: product being purchased
    @Column(name = "PRODUCT_ID", nullable = false)
    private int productId;

    // Amount in smallest currency unit (paise for INR)
    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // Amount actually paid (after discount/coupon if any)
    @Column(name = "AMOUNT_PAID", precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "CURRENCY", length = 10, nullable = false)
    private String currency = "INR";

    // CREATED | ATTEMPTED | PAID | FAILED | CANCELLED | REFUNDED
    @Column(name = "STATUS", length = 30, nullable = false)
    private String status = "CREATED";

    @Column(name = "RECEIPT", length = 100)
    private String receipt;

    // Coupon/promo code applied
    @Column(name = "COUPON_CODE", length = 50)
    private String couponCode;

    @Column(name = "DISCOUNT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    // JSON blob of Razorpay notes field
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "ATTEMPTS")
    private int attempts = 0;

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
