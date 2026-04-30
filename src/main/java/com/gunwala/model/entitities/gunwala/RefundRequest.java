package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

/** Sent by admin/client to initiate a refund */
@Data
public class RefundRequest {
    private String razorpayPaymentId;
    private BigDecimal amount;     // partial or full; null = full refund
    private String reason;         // optional
    private String speed;          // normal | instant; default normal
    private String notes;          // optional
}
