package com.gunwala.dto;

import com.gunwala.entity.Discount;
import com.gunwala.entity.DiscountScope;
import com.gunwala.entity.PromoValueType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DiscountResponse(
        Long id,
        String name,
        PromoValueType valueType,
        BigDecimal value,
        DiscountScope scope,
        List<Integer> productIds,
        Instant startsAt,
        Instant endsAt,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static DiscountResponse from(Discount d) {
        return new DiscountResponse(d.getId(), d.getName(), d.getValueType(), d.getValue(), d.getScope(),
                d.getProductIds().stream().sorted().toList(),
                d.getStartsAt(), d.getEndsAt(), d.isActive(), d.getCreatedAt(), d.getUpdatedAt());
    }
}
