package com.gunwala.model.entitities.gunwala;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private int productId;
    private int quantity;          // optional, default 1
    private String couponCode;     // optional
    private String currency;       // default INR
    private String notes;          // optional JSON string / free text
}
