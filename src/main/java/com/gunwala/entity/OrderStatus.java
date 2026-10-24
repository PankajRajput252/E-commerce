package com.gunwala.entity;

public enum OrderStatus {
    /** Order created at checkout; waiting for the buyer's payment to be approved. */
    PENDING_PAYMENT,
    /** Admin approved a payment for this order. */
    PAID,
    CANCELLED
}
