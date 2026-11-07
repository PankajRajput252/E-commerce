package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Coupon code a buyer enters at checkout. One per order, applied to the subtotal after sale discounts.
 * Uses are counted from shop_orders.voucher_id (see ShopOrderRepository.countVoucherUses).
 */
@Entity
@Table(name = "vouchers", uniqueConstraints = @UniqueConstraint(name = "uk_voucher_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Always upper-case A–Z / 0–9. */
    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromoValueType valueType;

    @Column(name = "voucher_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    /** Cap for PERCENTAGE vouchers; null = no cap. */
    @Column(precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    /** Subtotal (after sale discounts) needed; null = no minimum. */
    @Column(precision = 12, scale = 2)
    private BigDecimal minOrderAmount;

    /** Total uses across all buyers; null = unlimited. */
    private Integer usageLimit;

    /** Uses per buyer; null = unlimited. */
    private Integer perUserLimit;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(nullable = false)
    private boolean active = true;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public boolean isLiveAt(Instant now) {
        return active && !startsAt.isAfter(now) && (endsAt == null || endsAt.isAfter(now));
    }
}
