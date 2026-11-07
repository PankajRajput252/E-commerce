package com.gunwala.dto;

import com.gunwala.entity.PromoValueType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

/** Admin create/update of a voucher code. Cross-field rules are checked in VoucherService. */
public record VoucherRequest(
        @NotBlank(message = "Enter a code")
        @Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "4–20 letters or digits, no spaces")
        String code,
        @Size(max = 200) String description,
        @NotNull(message = "Choose % off or ₹ off") PromoValueType valueType,
        @NotNull(message = "Enter an amount")
        @DecimalMin(value = "0.01", message = "Enter an amount above 0")
        @Digits(integer = 10, fraction = 2, message = "Use up to 2 decimal places")
        BigDecimal value,
        @DecimalMin(value = "0.01", message = "Enter a rupee amount above 0")
        @Digits(integer = 10, fraction = 2, message = "Use up to 2 decimal places")
        BigDecimal maxDiscountAmount,
        @DecimalMin(value = "0.00", message = "Enter a rupee amount")
        @Digits(integer = 10, fraction = 2, message = "Use up to 2 decimal places")
        BigDecimal minOrderAmount,
        @Min(value = 1, message = "Whole number, 1 or more") Integer usageLimit,
        @Min(value = 1, message = "Whole number, 1 or more") Integer perUserLimit,
        @NotNull(message = "Choose when the voucher starts") Instant startsAt,
        Instant endsAt,
        boolean active
) {}
