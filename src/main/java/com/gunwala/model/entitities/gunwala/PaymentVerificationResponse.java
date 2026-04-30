package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

/** Returned after payment verification */
@Data
public class PaymentVerificationResponse {
    private boolean success;
    private String message;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String paymentStatus;      // CAPTURED | FAILED
    private BigDecimal amountPaid;
}
