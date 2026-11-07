package com.gunwala.entity;

/** How a discount or voucher reduces the price. */
public enum PromoValueType {
    /** value = percent off (0 < value < 100) */
    PERCENTAGE,
    /** value = rupees off */
    FLAT
}
