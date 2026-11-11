package com.gunwala.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Add to cart: quantity is added to what's already in the cart (default 1).
 * Set quantity: productId is taken from the path and quantity replaces the current amount.
 */
public record CartItemRequest(
        Integer productId,
        @NotNull @Min(1) @Max(99) Integer quantity
) {}
