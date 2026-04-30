package com.gunwala.model.entitities.gunwala;

import lombok.Data;

/** Sent by the client after Razorpay checkout success */
@Data
public class VerifyPaymentRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
