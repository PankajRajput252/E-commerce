package com.gunwala.dto;

import com.gunwala.entity.DiscountScope;
import com.gunwala.entity.PromoValueType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Admin create/update of an automatic sale. Cross-field rules are checked in DiscountService. */
public record DiscountRequest(
        @NotBlank(message = "Give the sale a name") @Size(max = 100) String name,
        @NotNull(message = "Choose % off or ₹ off") PromoValueType valueType,
        @NotNull(message = "Enter an amount")
        @DecimalMin(value = "0.01", message = "Enter an amount above 0")
        @Digits(integer = 10, fraction = 2, message = "Use up to 2 decimal places")
        BigDecimal value,
        @NotNull(message = "Choose what the sale applies to") DiscountScope scope,
        @Size(max = 1000, message = "Too many products in one sale") List<@NotNull Integer> productIds,
        @NotNull(message = "Choose when the sale starts") Instant startsAt,
        Instant endsAt,
        boolean active
) {}
