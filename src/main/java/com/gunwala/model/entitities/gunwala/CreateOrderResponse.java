package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

/** Returned to the client so it can open Razorpay checkout */
@Data
public class CreateOrderResponse {
    private String razorpayOrderId;
    private BigDecimal amount;         // in paise (multiply INR × 100)
    private String currency;
    private String status;
    private String receipt;
    private String razorpayKeyId;      // public key for the checkout widget
}
