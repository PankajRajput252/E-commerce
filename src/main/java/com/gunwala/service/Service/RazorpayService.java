package com.gunwala.service.Service;

import com.gunwala.config.RazorpayConfig;
import com.gunwala.model.User;
import com.gunwala.model.entitities.gunwala.*;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.repo.gunwala.RazorpayOrderRepository;
import com.gunwala.repo.gunwala.RazorpayPaymentRepository;
import com.gunwala.repo.gunwala.RazorpayRefundRepository;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.apache.commons.codec.binary.Hex; // if using Apache Commons
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    private final RazorpayOrderRepository orderRepository;
    private final RazorpayPaymentRepository paymentRepository;
    private final RazorpayRefundRepository refundRepository;
    private final ProductRepository productRepository;

    // ─────────────────────────────────────────────────────────────
    //  1. CREATE ORDER
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, User currentUser) {

        // Fetch product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.getProductId()));

        int qty = request.getQuantity() > 0 ? request.getQuantity() : 1;

        // Compute final amount (apply coupon if present)
        BigDecimal baseAmount = product.getPrice().multiply(BigDecimal.valueOf(qty));
        BigDecimal discount   = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            discount = resolveCouponDiscount(request.getCouponCode(), baseAmount);
        }

        BigDecimal finalAmount = baseAmount.subtract(discount).max(BigDecimal.ZERO);

        // Razorpay expects amount in smallest unit (paise)
        long amountInPaise = finalAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP).longValue();

        String currency = request.getCurrency() != null ? request.getCurrency()
                : razorpayConfig.getDefaultCurrency();

        String receipt = "RCPT_" + currentUser.getUserPkId() + "_" + System.currentTimeMillis();

        // Build Razorpay order options
        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", currency);
        options.put("receipt", receipt);
        options.put("payment_capture", 1);   // auto-capture

        JSONObject notes = new JSONObject();
        notes.put("userId", currentUser.getUserPkId());
        notes.put("productId", product.getProductPkId());
        if (request.getNotes() != null) notes.put("extra", request.getNotes());
        options.put("notes", notes);

        try {
            Order rzpOrder = razorpayClient.orders.create(options);

            // Persist to DB
            RazorpayOrder dbOrder = new RazorpayOrder();
            dbOrder.setRazorpayOrderId(rzpOrder.get("id"));
            dbOrder.setUserId(currentUser.getUserPkId());
            dbOrder.setProductId(product.getProductPkId());
            dbOrder.setAmount(finalAmount);
            dbOrder.setAmountPaid(BigDecimal.ZERO);
            dbOrder.setCurrency(currency);
            dbOrder.setStatus("CREATED");
            dbOrder.setReceipt(receipt);
            dbOrder.setCouponCode(request.getCouponCode());
            dbOrder.setDiscountAmount(discount);
            dbOrder.setNotes(notes.toString());
            orderRepository.save(dbOrder);

            // Build response
            CreateOrderResponse response = new CreateOrderResponse();
            response.setRazorpayOrderId(rzpOrder.get("id"));
            response.setAmount(finalAmount);
            response.setCurrency(currency);
            response.setStatus("CREATED");
            response.setReceipt(receipt);
            response.setRazorpayKeyId(razorpayConfig.getKeyId());
            return response;

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Payment order creation failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  2. VERIFY PAYMENT (called after frontend checkout)
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public PaymentVerificationResponse verifyPayment(VerifyPaymentRequest request, User currentUser) {

        // 1. Verify HMAC-SHA256 signature
        boolean signatureValid = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        // 2. Fetch payment details from Razorpay
        Payment rzpPayment;
        try {
            rzpPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
        } catch (RazorpayException e) {
            log.error("Failed to fetch Razorpay payment: {}", e.getMessage(), e);
            throw new RuntimeException("Could not fetch payment details from Razorpay");
        }

        // 3. Find our order record
        RazorpayOrder dbOrder = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getRazorpayOrderId()));

        String rzpStatus = rzpPayment.get("status");    // authorized | captured | failed
        BigDecimal amountPaid = new BigDecimal(rzpPayment.get("amount").toString())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 4. Persist payment record
        RazorpayPayment dbPayment = new RazorpayPayment();
        dbPayment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        dbPayment.setRazorpayOrderId(request.getRazorpayOrderId());
        dbPayment.setOrderId(dbOrder.getOrderPkId());
        dbPayment.setUserId(currentUser.getUserPkId());
        dbPayment.setProductId(dbOrder.getProductId());
        dbPayment.setAmount(amountPaid);
        dbPayment.setCurrency(rzpPayment.get("currency"));
        dbPayment.setStatus(rzpStatus.toUpperCase());
        dbPayment.setMethod(safeGet(rzpPayment, "method"));
        dbPayment.setVpa(safeGet(rzpPayment, "vpa"));
        dbPayment.setBank(safeGet(rzpPayment, "bank"));
        dbPayment.setWallet(safeGet(rzpPayment, "wallet"));
        dbPayment.setRazorpaySignature(request.getRazorpaySignature());
        dbPayment.setSignatureValid(signatureValid);

        if ("captured".equalsIgnoreCase(rzpStatus)) {
            dbPayment.setCapturedAt(LocalDateTime.now());
        }

        String errorCode = safeGet(rzpPayment, "error_code");
        if (errorCode != null) {
            dbPayment.setErrorCode(errorCode);
            dbPayment.setErrorDescription(safeGet(rzpPayment, "error_description"));
            dbPayment.setErrorReason(safeGet(rzpPayment, "error_reason"));
            dbPayment.setErrorSource(safeGet(rzpPayment, "error_source"));
            dbPayment.setErrorStep(safeGet(rzpPayment, "error_step"));
        }

        paymentRepository.save(dbPayment);

        // 5. Update order status
        if (signatureValid && "captured".equalsIgnoreCase(rzpStatus)) {
            dbOrder.setStatus("PAID");
            dbOrder.setAmountPaid(amountPaid);
        } else {
            dbOrder.setStatus("FAILED");
        }
        dbOrder.setAttempts(dbOrder.getAttempts() + 1);
        orderRepository.save(dbOrder);

        // 6. Build response
        PaymentVerificationResponse response = new PaymentVerificationResponse();
        response.setSuccess(signatureValid && "captured".equalsIgnoreCase(rzpStatus));
        response.setMessage(response.isSuccess() ? "Payment successful" : "Payment verification failed");
        response.setRazorpayPaymentId(request.getRazorpayPaymentId());
        response.setRazorpayOrderId(request.getRazorpayOrderId());
        response.setPaymentStatus(rzpStatus.toUpperCase());
        response.setAmountPaid(amountPaid);
        return response;
    }

    // ─────────────────────────────────────────────────────────────
    //  3. INITIATE REFUND
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public RefundResponse initiateRefund(RefundRequest request, User currentUser) {

        // Find original payment
        RazorpayPayment dbPayment = paymentRepository
                .findByRazorpayPaymentId(request.getRazorpayPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found: " + request.getRazorpayPaymentId()));

        if (!"CAPTURED".equalsIgnoreCase(dbPayment.getStatus())) {
            throw new RuntimeException("Only captured payments can be refunded. Current status: " + dbPayment.getStatus());
        }

        BigDecimal refundAmount = request.getAmount() != null
                ? request.getAmount()
                : dbPayment.getAmount();   // full refund if amount not specified

        long refundInPaise = refundAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP).longValue();

        JSONObject refundOptions = new JSONObject();
        refundOptions.put("amount", refundInPaise);
        refundOptions.put("speed", request.getSpeed() != null ? request.getSpeed() : "normal");

        JSONObject notes = new JSONObject();
        notes.put("reason", request.getReason() != null ? request.getReason() : "Customer request");
        notes.put("initiatedBy", currentUser.getUserPkId());
        if (request.getNotes() != null) notes.put("extra", request.getNotes());
        refundOptions.put("notes", notes);

        try {
            Refund rzpRefund = razorpayClient.payments.refund(request.getRazorpayPaymentId(), refundOptions);

            // Persist refund
            RazorpayRefund dbRefund = new RazorpayRefund();
            dbRefund.setRazorpayRefundId(rzpRefund.get("id"));
            dbRefund.setRazorpayPaymentId(request.getRazorpayPaymentId());
            dbRefund.setPaymentId(dbPayment.getPaymentPkId());
            dbRefund.setOrderId(dbPayment.getOrderId());
            dbRefund.setUserId(dbPayment.getUserId());
            dbRefund.setAmount(refundAmount);
            dbRefund.setCurrency(dbPayment.getCurrency());
            dbRefund.setStatus(((String) rzpRefund.get("status")).toUpperCase());
            dbRefund.setSpeedRequested(request.getSpeed() != null ? request.getSpeed() : "normal");
            dbRefund.setReason(request.getReason());
            dbRefund.setInitiatedBy("USER:" + currentUser.getUserPkId());
            dbRefund.setNotes(notes.toString());
            refundRepository.save(dbRefund);

            // Mark payment as refunded
            dbPayment.setStatus("REFUNDED");
            paymentRepository.save(dbPayment);

            // Update order status
            orderRepository.findById(dbPayment.getOrderId()).ifPresent(o -> {
                o.setStatus("REFUNDED");
                orderRepository.save(o);
            });

            RefundResponse response = new RefundResponse();
            response.setSuccess(true);
            response.setRazorpayRefundId(rzpRefund.get("id"));
            response.setRazorpayPaymentId(request.getRazorpayPaymentId());
            response.setAmount(refundAmount);
            response.setStatus(((String) rzpRefund.get("status")).toUpperCase());
            response.setSpeed(rzpRefund.get("speed_processed"));
            return response;

        } catch (RazorpayException e) {
            log.error("Refund initiation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  4. FETCH HELPERS
    // ─────────────────────────────────────────────────────────────

    public List<RazorpayPayment> getPaymentsByUser(int userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<RazorpayOrder> getOrdersByUser(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public RazorpayPayment getPaymentByRazorpayId(String razorpayPaymentId) {
        return paymentRepository.findByRazorpayPaymentId(razorpayPaymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + razorpayPaymentId));
    }

    // ─────────────────────────────────────────────────────────────
    //  5. SIGNATURE VERIFICATION
    // ─────────────────────────────────────────────────────────────

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String data   = orderId + "|" + paymentId;
            Mac mac       = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    razorpayConfig.getKeySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash   = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean verifyWebhookSignature(String payload, String razorpaySignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    razorpayConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash     = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(hash);
            return computed.equals(razorpaySignature);
        } catch (Exception e) {
            log.error("Webhook signature verification error: {}", e.getMessage(), e);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    /** Stub — wire in your actual coupon/promo service */
    private BigDecimal resolveCouponDiscount(String couponCode, BigDecimal baseAmount) {
        // TODO: query coupon table, validate, return discount amount
        log.info("Resolving coupon {} for base amount {}", couponCode, baseAmount);
        return BigDecimal.ZERO;
    }

    private String safeGet(Payment payment, String key) {
        try {
            Object val = payment.get(key);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
