package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

/** Generic payment detail DTO */
@Data
public class PaymentDetailResponse {
    private int paymentPkId;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String method;
    private String vpa;
    private String cardNetwork;
    private String bank;
    private boolean signatureValid;
    private String createdAt;
}
