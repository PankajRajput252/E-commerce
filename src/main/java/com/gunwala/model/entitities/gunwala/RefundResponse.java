package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

/** Returned after a refund is initiated */
@Data
public class RefundResponse {
    private boolean success;
    private String razorpayRefundId;
    private String razorpayPaymentId;
    private BigDecimal amount;
    private String status;
    private String speed;
}
