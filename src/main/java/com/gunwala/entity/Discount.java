package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Automatic sale set by the admin (no code needed). While live it lowers the unit price of the store
 * products it covers; when several cover one product the lowest resulting price wins (they never stack).
 */
@Entity
@Table(name = "discounts", indexes = @Index(name = "idx_discount_live", columnList = "active, starts_at, ends_at"))
@Getter
@Setter
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromoValueType valueType;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountScope scope;

    /** Empty when scope is ALL_STORE_PRODUCTS. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "discount_products", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "product_id", nullable = false)
    @BatchSize(size = 50)
    private Set<Integer> productIds = new HashSet<>();

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    /** null = no end date. */
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

    public boolean covers(int productId) {
        return scope == DiscountScope.ALL_STORE_PRODUCTS || productIds.contains(productId);
    }
}
