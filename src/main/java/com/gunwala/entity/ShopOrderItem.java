package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** One product line. Name, price and licence flag are copied from the product when the order is placed. */
@Entity
@Table(name = "shop_order_items")
@Getter
@Setter
@NoArgsConstructor
public class ShopOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_order_id", nullable = false)
    private ShopOrder order;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private int units;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private boolean licenceRequired;

    /** true = sold by Bandookwale's own store (ships from our pickup address); false = listed by another seller. */
    @Column(nullable = false)
    private boolean storeProduct = true;

    /** SKU sent to Shiprocket. */
    public String getSku() {
        return "BW-P" + productId;
    }
}
