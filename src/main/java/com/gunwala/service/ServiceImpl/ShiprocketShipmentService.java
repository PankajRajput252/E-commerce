package com.gunwala.service.ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunwala.shipRocket.ShiprocketServiceProxy;
import com.gunwala.shipRocket.model.*;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.function.Function;

/**
 * Thin wrapper around the Shiprocket API: adds the auth token and retries once with a fresh token on 401.
 * Used by the Razorpay flow (OrderController) and by the manual-payment auto-ship flow.
 */
@Slf4j
@Service
public class ShiprocketShipmentService {

    @Autowired
    private ShiprocketServiceProxy shiprocketServiceProxy;

    @Autowired
    private ShiprocketTokenManager tokenManager;

    @Autowired
    private ObjectMapper objectMapper;

    /** Must match a pickup address nickname in Shiprocket → Settings → Pickup Addresses. */
    @Value("${shiprocket.pickup-location}")
    private String pickupLocation;

    /**
     * STEP 1: create the order in Shiprocket. Returns Shiprocket's order_id and shipment_id.
     * Shiprocket sometimes reports errors with HTTP 200 (e.g. a wrong pickup location), so the raw reply
     * is read and, if it has no order/shipment id, its message is thrown so the admin can see it.
     */
    public OrderResponse createOrder(OrderRequestBody orderRequestBody) {
        if (!StringUtils.hasText(orderRequestBody.getPickup_location())) {
            orderRequestBody.setPickup_location(pickupLocation);
        }
        String raw = withToken(h -> shiprocketServiceProxy.createShiprocketOrderRaw(h, orderRequestBody));
        log.info("Shiprocket create-order reply for {} (pickup_location='{}'): {}",
                orderRequestBody.getOrder_id(), orderRequestBody.getPickup_location(), abbreviate(raw, 2000));

        OrderResponse response;
        try {
            response = objectMapper.readValue(raw, OrderResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected reply from Shiprocket: " + abbreviate(raw, 600));
        }
        if (response == null || response.getOrderId() == null || response.getShipmentId() == null) {
            throw new IllegalStateException("Shiprocket did not create the order: " + shiprocketMessage(raw)
                    + " [pickup_location used: '" + orderRequestBody.getPickup_location() + "']");
        }
        return response;
    }

    /** Pulls "message" and "errors" out of a Shiprocket reply; falls back to the raw text. */
    private String shiprocketMessage(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "empty reply";
        }
        try {
            JsonNode node = objectMapper.readTree(raw);
            StringBuilder sb = new StringBuilder();
            if (node.hasNonNull("message")) sb.append(node.get("message").asText());
            if (node.hasNonNull("errors")) sb.append(sb.length() > 0 ? " – " : "").append(node.get("errors").toString());
            if (node.hasNonNull("status_code")) sb.append(" (status_code ").append(node.get("status_code").asText()).append(")");
            return sb.length() > 0 ? abbreviate(sb.toString(), 700) : abbreviate(raw, 700);
        } catch (Exception e) {
            return abbreviate(raw, 700);
        }
    }

    /**
     * STEP 2: assign a courier and generate the AWB (tracking number).
     * Pass a blank courierId to let Shiprocket pick the recommended courier. Deducts from the Shiprocket wallet.
     */
    public AwbResponseAssaign generateAwb(String shipmentId, String courierId) {
        AwbRequestBody body = new AwbRequestBody();
        body.setShipmentId(shipmentId);
        body.setCourierId(StringUtils.hasText(courierId) ? courierId : null);
        return withToken(h -> shiprocketServiceProxy.generateAwb(h, body));
    }

    /** STEP 3: ask the courier to pick the parcel up. */
    public PickupResponse generatePickup(String shipmentId) {
        PickupRequestBody body = new PickupRequestBody(Long.valueOf(shipmentId));
        return withToken(h -> shiprocketServiceProxy.generatePickup(h, body));
    }

    /** Track a shipment by Shiprocket shipment id. */
    public TrackingReaponse trackShipment(String shipmentId) {
        return withToken(h -> shiprocketServiceProxy.trackShipment(shipmentId, h));
    }

    /**
     * Razorpay flow: create the order, then try AWB + pickup. The order counts as created even if
     * AWB/pickup fail; those can be retried from /api/shiprocket/generate-awb and /generate-pickup.
     */
    public OrderResponse createOrderAndShip(OrderRequestBody orderRequestBody) {
        OrderResponse orderResponse = createOrder(orderRequestBody);
        Long shipmentId = orderResponse == null ? null : orderResponse.getShipmentId();
        if (shipmentId != null) {
            try {
                generateAwb(String.valueOf(shipmentId), null);
                generatePickup(String.valueOf(shipmentId));
            } catch (Exception e) {
                log.warn("Shiprocket order {} created but AWB/pickup failed: {}", orderRequestBody.getOrder_id(), describe(e));
            }
        }
        return orderResponse;
    }

    /** Calls Shiprocket with a token; on 401 refreshes the token and tries once more. */
    private <T> T withToken(Function<Map<String, String>, T> call) {
        try {
            return call.apply(tokenManager.authHeaders());
        } catch (FeignException.Unauthorized e) {
            tokenManager.invalidateToken();
            return call.apply(tokenManager.authHeaders());
        }
    }

    /** Short, admin-readable description of a Shiprocket error (status + response body). */
    public static String describe(Throwable e) {
        if (e instanceof FeignException fe) {
            String body = fe.contentUTF8();
            String detail = StringUtils.hasText(body) ? body : fe.getMessage();
            return "Shiprocket HTTP " + fe.status() + ": " + abbreviate(detail, 800);
        }
        return abbreviate(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(), 800);
    }

    private static String abbreviate(String s, int max) {
        if (s == null) return null;
        String oneLine = s.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= max ? oneLine : oneLine.substring(0, max) + "…";
    }
}
