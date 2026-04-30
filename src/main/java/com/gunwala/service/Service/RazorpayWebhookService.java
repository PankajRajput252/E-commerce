package com.gunwala.service.Service;

import com.gunwala.model.entitities.gunwala.RazorpayPayment;
import com.gunwala.model.entitities.gunwala.RazorpayWebhookEvent;
import com.gunwala.repo.gunwala.RazorpayOrderRepository;
import com.gunwala.repo.gunwala.RazorpayPaymentRepository;
import com.gunwala.repo.gunwala.RazorpayWebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayWebhookService {
    private final RazorpayService razorpayService;
    private final RazorpayWebhookEventRepository webhookEventRepository;
    private final RazorpayOrderRepository orderRepository;
    private final RazorpayPaymentRepository paymentRepository;

    @Transactional
    public void handleWebhook(String payload, String signature) {

        // ── 1. Verify signature ──────────────────────────────────
        boolean sigValid = razorpayService.verifyWebhookSignature(payload, signature);

        JSONObject json       = new JSONObject(payload);
        String eventId        = json.optString("id");
        String eventType      = json.optString("event");
        long   tsEpochSeconds = json.optLong("created_at", 0);

        // ── 2. Idempotency: skip duplicates ─────────────────────
        if (webhookEventRepository.existsByRazorpayEventId(eventId)) {
            log.warn("Duplicate webhook event received: {}", eventId);
            webhookEventRepository.findByRazorpayEventId(eventId).ifPresent(e -> {
                e.setStatus("DUPLICATE");
                webhookEventRepository.save(e);
            });
            return;
        }

        // ── 3. Persist raw event ─────────────────────────────────
        RazorpayWebhookEvent event = new RazorpayWebhookEvent();
        event.setRazorpayEventId(eventId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setWebhookSignature(signature);
        event.setSignatureValid(sigValid);
        event.setStatus("RECEIVED");
        if (tsEpochSeconds > 0) {
            event.setEventTimestamp(LocalDateTime.ofEpochSecond(tsEpochSeconds, 0,
                    java.time.ZoneOffset.UTC));
        }
        webhookEventRepository.save(event);

        if (!sigValid) {
            log.error("Invalid webhook signature for event: {}", eventId);
            event.setStatus("FAILED");
            event.setErrorMessage("Signature verification failed");
            webhookEventRepository.save(event);
            return;
        }

        // ── 4. Route by event type ───────────────────────────────
        try {
            JSONObject payload_json = json.getJSONObject("payload");


            switch (eventType) {
                case "payment.captured":
                    JSONObject capturedPayment = payload_json
                            .getJSONObject("payment").getJSONObject("entity");
                    handlePaymentCaptured(capturedPayment);
                    break;

                case "payment.failed":
                    JSONObject failedPayment = payload_json
                            .getJSONObject("payment").getJSONObject("entity");
                    handlePaymentFailed(failedPayment);
                    break;

                case "order.paid":
                    JSONObject orderObj = payload_json
                            .getJSONObject("order").getJSONObject("entity");
                    handleOrderPaid(orderObj);
                    break;

                case "refund.created":
                case "refund.processed":
                    JSONObject refundObj = payload_json
                            .getJSONObject("refund").getJSONObject("entity");
                    handleRefundEvent(refundObj, eventType);
                    break;

                default:
                    log.info("Unhandled webhook event type: {}", eventType);
                    break;
            }

            event.setStatus("PROCESSED");
            event.setProcessedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Webhook processing error for event {}: {}", eventId, e.getMessage(), e);
            event.setStatus("FAILED");
            event.setErrorMessage(e.getMessage());
            event.setRetryCount(event.getRetryCount() + 1);
        }

        webhookEventRepository.save(event);
    }

    // ─────────────────────────────────────────────────────────────

    private void handlePaymentCaptured(JSONObject paymentEntity) {
        String rzpPaymentId = paymentEntity.getString("id");
        String rzpOrderId   = paymentEntity.getString("order_id");

        paymentRepository.findByRazorpayPaymentId(rzpPaymentId).ifPresentOrElse(p -> {
            p.setStatus("CAPTURED");
            p.setCapturedAt(LocalDateTime.now());
            paymentRepository.save(p);
        }, () -> {
            // Payment record might not exist if webhook fires before our verify call
            RazorpayPayment p = new RazorpayPayment();
            p.setRazorpayPaymentId(rzpPaymentId);
            p.setRazorpayOrderId(rzpOrderId);
            p.setStatus("CAPTURED");
            p.setCapturedAt(LocalDateTime.now());
            BigDecimal amt = new BigDecimal(paymentEntity.optInt("amount", 0))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            p.setAmount(amt);
            p.setCurrency(paymentEntity.optString("currency", "INR"));
            p.setMethod(paymentEntity.optString("method"));
            paymentRepository.save(p);
        });

        // Sync order status
        orderRepository.findByRazorpayOrderId(rzpOrderId).ifPresent(o -> {
            o.setStatus("PAID");
            orderRepository.save(o);
        });
    }

    private void handlePaymentFailed(JSONObject paymentEntity) {
        String rzpPaymentId = paymentEntity.getString("id");
        paymentRepository.findByRazorpayPaymentId(rzpPaymentId).ifPresent(p -> {
            p.setStatus("FAILED");
            p.setErrorCode(paymentEntity.optString("error_code"));
            p.setErrorDescription(paymentEntity.optString("error_description"));
            p.setErrorReason(paymentEntity.optString("error_reason"));
            paymentRepository.save(p);
        });
    }

    private void handleOrderPaid(JSONObject orderEntity) {
        String rzpOrderId = orderEntity.getString("id");
        orderRepository.findByRazorpayOrderId(rzpOrderId).ifPresent(o -> {
            o.setStatus("PAID");
            orderRepository.save(o);
        });
    }

    private void handleRefundEvent(JSONObject refundEntity, String eventType) {
        // Refund-specific logic (e.g., update refund status, notify user)
        log.info("Refund event [{}] received: {}", eventType, refundEntity.optString("id"));
        // Wire in your notification service here if needed
    }
}
