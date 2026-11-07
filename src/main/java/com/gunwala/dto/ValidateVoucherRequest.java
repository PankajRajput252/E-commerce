package com.gunwala.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Buyer checks a voucher against the cart (prices come from the products table). */
public record ValidateVoucherRequest(
        @NotBlank(message = "Enter a voucher code") @Size(max = 20) String code,
        @NotEmpty(message = "Your cart is empty")
        @Size(max = 50, message = "Too many different items in one order")
        @Valid List<CreateShopOrderRequest.Item> items
) {}
