package com.gunwala.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * The buyer's cart with live prices. Items whose product was removed or deactivated
 * are dropped from the cart; sold-out items stay with available=false so the buyer sees why.
 */
public record CartResponse(
        List<Line> items,
        int totalItems,
        BigDecimal subtotal
) {
    public record Line(
            Integer productId,
            String title,
            String image,
            String location,
            int quantity,
            /* Price per unit the buyer pays now (after any live sale discount). */
            BigDecimal unitPrice,
            /* List price when a sale lowered it; null otherwise. */
            BigDecimal originalUnitPrice,
            /* Units in stock; null when the product doesn't track stock. */
            Integer stock,
            boolean available
    ) {}
}
