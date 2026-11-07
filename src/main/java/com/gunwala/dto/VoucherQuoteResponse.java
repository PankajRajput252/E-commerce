package com.gunwala.dto;

import java.math.BigDecimal;

/** What the voucher would take off the current cart. */
public record VoucherQuoteResponse(
        String code,
        String description,
        /* cart total after sale discounts */ BigDecimal subtotal,
        BigDecimal discountAmount,
        /* subtotal − discountAmount */ BigDecimal total
) {}
