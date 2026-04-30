package com.gunwala.controller;

import com.gunwala.model.User;
import com.gunwala.model.entitities.gunwala.*;
import com.gunwala.service.Service.RazorpayService;
import com.gunwala.service.Service.RazorpayWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class RazorpayController {
    private final RazorpayService razorpayService;
    private final RazorpayWebhookService webhookService;

    // ─────────────────────────────────────────────────────────────
    //  POST /api/v1/payments/orders
    //  Create a Razorpay order and return the key + order ID to the frontend
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal User currentUser) {

        log.info("Creating order for user={} product={}", currentUser.getUserPkId(), request.getProductId());
        CreateOrderResponse response = razorpayService.createOrder(request, currentUser);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────
    //  POST /api/v1/payments/verify
    //  Frontend calls this after Razorpay checkout handler.success()
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody VerifyPaymentRequest request,
            @AuthenticationPrincipal User currentUser) {

        log.info("Verifying payment orderId={} paymentId={}",
                request.getRazorpayOrderId(), request.getRazorpayPaymentId());
        PaymentVerificationResponse response = razorpayService.verifyPayment(request, currentUser);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────
    //  POST /api/v1/payments/refund
    //  Initiate a full or partial refund
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> initiateRefund(
            @RequestBody RefundRequest request,
            @AuthenticationPrincipal User currentUser) {

        log.info("Refund request paymentId={} by user={}", request.getRazorpayPaymentId(), currentUser.getUserPkId());
        RefundResponse response = razorpayService.initiateRefund(request, currentUser);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────
    //  GET /api/v1/payments/my-payments
    //  Logged-in user's payment history
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/my-payments")
    public ResponseEntity<List<RazorpayPayment>> getMyPayments(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(razorpayService.getPaymentsByUser(currentUser.getUserPkId()));
    }

    // ─────────────────────────────────────────────────────────────
    //  GET /api/v1/payments/my-orders
    //  Logged-in user's order history
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/my-orders")
    public ResponseEntity<List<RazorpayOrder>> getMyOrders(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(razorpayService.getOrdersByUser(currentUser.getUserPkId()));
    }

    // ─────────────────────────────────────────────────────────────
    //  GET /api/v1/payments/{razorpayPaymentId}
    //  Get a single payment record
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/{razorpayPaymentId}")
    public ResponseEntity<RazorpayPayment> getPayment(
            @PathVariable String razorpayPaymentId) {

        return ResponseEntity.ok(razorpayService.getPaymentByRazorpayId(razorpayPaymentId));
    }

    // ─────────────────────────────────────────────────────────────
    //  POST /api/v1/payments/webhook
    //  Razorpay webhook endpoint — no auth, signature-verified internally
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        log.info("Webhook received, signature present={}", signature != null);
        webhookService.handleWebhook(payload, signature);
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
