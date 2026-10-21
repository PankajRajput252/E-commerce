package com.gunwala.event;


import com.gunwala.entity.PaymentStatus;

public record PaymentStatusChangedEvent(Long paymentId, String userId, PaymentStatus status, String rejectionReason) {}
