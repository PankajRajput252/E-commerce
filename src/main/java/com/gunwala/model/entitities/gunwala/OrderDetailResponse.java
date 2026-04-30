package com.gunwala.model.entitities.gunwala;


import lombok.Data;

import java.math.BigDecimal;

/** Generic order detail DTO */
@Data
public class OrderDetailResponse {
    private int orderPkId;
    private String razorpayOrderId;
    private int productId;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private String currency;
    private String status;
    private String couponCode;
    private BigDecimal discountAmount;
    private int attempts;
    private String createdAt;
}
