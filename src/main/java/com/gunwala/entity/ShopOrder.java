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
import java.util.ArrayList;
import java.util.List;

/**
 * A checkout order paid by bank transfer / UPI. Created before the payment is submitted, so the
 * shipping address and items are stored on the server and are ready when the admin approves the payment.
 */
@Entity
@Table(name = "shop_orders", indexes = {
        @Index(name = "idx_so_user", columnList = "user_id"),
        @Index(name = "idx_so_status", columnList = "status"),
        @Index(name = "idx_so_shipping_status", columnList = "shipping_status")
})
@Getter
@Setter
@NoArgsConstructor
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Public order id, e.g. "BW-20261024-7F3K9Q". Also sent to Shiprocket as order_id. */
    @Column(name = "order_reference", nullable = false, unique = true, length = 50)
    private String orderReference;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @BatchSize(size = 50)
    private List<ShopOrderItem> items = new ArrayList<>();

    // ---- Shipping address ----
    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 200)
    private String addressLine1;

    @Column(length = 200)
    private String addressLine2;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 80)
    private String state;

    @Column(nullable = false, length = 6)
    private String pincode;

    @Column(nullable = false, length = 60)
    private String country = "India";

    // ---- Payment link ----
    private Long paidPaymentSubmissionId;
    private Instant paidAt;

    // ---- Shiprocket ----
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false, length = 20)
    private ShippingStatus shippingStatus = ShippingStatus.NOT_STARTED;

    private Long shiprocketOrderId;
    private Long shiprocketShipmentId;

    @Column(length = 50)
    private String awbCode;

    @Column(length = 100)
    private String courierName;

    @Column(length = 50)
    private String pickupScheduledDate;

    @Column(length = 1000)
    private String shippingError;

    private int shippingAttempts;
    private Instant lastShippingAttemptAt;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addItem(ShopOrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public boolean containsLicenceRequiredItem() {
        return items.stream().anyMatch(ShopOrderItem::isLicenceRequired);
    }

    public boolean containsThirdPartySellerItem() {
        return items.stream().anyMatch(i -> !i.isStoreProduct());
    }
}
