package com.gunwala.dto;

import com.gunwala.entity.PromoValueType;
import com.gunwala.entity.Voucher;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherResponse(
        Long id,
        String code,
        String description,
        PromoValueType valueType,
        BigDecimal value,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount,
        Integer usageLimit,
        Integer perUserLimit,
        Instant startsAt,
        Instant endsAt,
        boolean active,
        /* paid orders + orders with a submitted (not rejected) payment */ long usedCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static VoucherResponse from(Voucher v, long usedCount) {
        return new VoucherResponse(v.getId(), v.getCode(), v.getDescription(), v.getValueType(), v.getValue(),
                v.getMaxDiscountAmount(), v.getMinOrderAmount(), v.getUsageLimit(), v.getPerUserLimit(),
                v.getStartsAt(), v.getEndsAt(), v.isActive(), usedCount, v.getCreatedAt(), v.getUpdatedAt());
    }
}
