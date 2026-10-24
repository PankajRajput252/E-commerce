package com.gunwala.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * Checkout request. Only product ids and quantities come from the browser:
 * names, prices and the licence flag are read from the products table.
 */
public record CreateShopOrderRequest(
        @NotEmpty(message = "Your cart is empty")
        @Size(max = 50, message = "Too many different items in one order")
        List<@Valid Item> items,

        @NotNull(message = "Shipping address is required")
        @Valid
        ShippingAddress shipping
) {
    public record Item(
            @NotNull Integer productId,
            @NotNull @Min(1) @Max(99) Integer quantity
    ) {}

    public record ShippingAddress(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Pattern(regexp = "^(\\+?91[\\s-]?|0)?[6-9]\\d{9}$", message = "must be a valid 10-digit Indian mobile number") String phone,
            @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 5, max = 200) String addressLine1,
            @Size(max = 200) String addressLine2,
            @NotBlank @Size(max = 80) String city,
            @NotBlank @Size(max = 80) String state,
            @NotBlank @Pattern(regexp = "^[1-9]\\d{5}$", message = "must be a valid 6-digit PIN code") String pincode
    ) {}
}
