package com.gunwala.model.entitities.gunwala;

import lombok.Data;

import java.math.BigDecimal;

public class RazorpayDTO {
//    /** Sent by the client to create a Razorpay order */
//    @Data
//    public static class CreateOrderRequest {
//        private int productId;
//        private int quantity;          // optional, default 1
//        private String couponCode;     // optional
//        private String currency;       // default INR
//        private String notes;          // optional JSON string / free text
//    }
//
//    /** Sent by the client after Razorpay checkout success */
//    @Data
//    public static class VerifyPaymentRequest {
//        private String razorpayOrderId;
//        private String razorpayPaymentId;
//        private String razorpaySignature;
//    }
//
//    /** Sent by admin/client to initiate a refund */
//    @Data
//    public static class RefundRequest {
//        private String razorpayPaymentId;
//        private BigDecimal amount;     // partial or full; null = full refund
//        private String reason;         // optional
//        private String speed;          // normal | instant; default normal
//        private String notes;          // optional
//    }
//
//    // ─────────────────────────────────────────────
//    //  RESPONSE DTOs
//    // ─────────────────────────────────────────────
//
//    /** Returned to the client so it can open Razorpay checkout */
//    @Data
//    public static class CreateOrderResponse {
//        private String razorpayOrderId;
//        private BigDecimal amount;         // in paise (multiply INR × 100)
//        private String currency;
//        private String status;
//        private String receipt;
//        private String razorpayKeyId;      // public key for the checkout widget
//    }
//
//    /** Returned after payment verification */
//    @Data
//    public static class PaymentVerificationResponse {
//        private boolean success;
//        private String message;
//        private String razorpayPaymentId;
//        private String razorpayOrderId;
//        private String paymentStatus;      // CAPTURED | FAILED
//        private BigDecimal amountPaid;
//    }
//
//    /** Returned after a refund is initiated */
//    @Data
//    public static class RefundResponse {
//        private boolean success;
//        private String razorpayRefundId;
//        private String razorpayPaymentId;
//        private BigDecimal amount;
//        private String status;
//        private String speed;
//    }
//
//    /** Generic payment detail DTO */
//    @Data
//    public static class PaymentDetailResponse {
//        private int paymentPkId;
//        private String razorpayPaymentId;
//        private String razorpayOrderId;
//        private BigDecimal amount;
//        private String currency;
//        private String status;
//        private String method;
//        private String vpa;
//        private String cardNetwork;
//        private String bank;
//        private boolean signatureValid;
//        private String createdAt;
//    }
//
//    /** Generic order detail DTO */
//    @Data
//    public static class OrderDetailResponse {
//        private int orderPkId;
//        private String razorpayOrderId;
//        private int productId;
//        private BigDecimal amount;
//        private BigDecimal amountPaid;
//        private String currency;
//        private String status;
//        private String couponCode;
//        private BigDecimal discountAmount;
//        private int attempts;
//        private String createdAt;
//    }
}
