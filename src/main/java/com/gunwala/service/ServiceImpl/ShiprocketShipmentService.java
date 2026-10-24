package com.gunwala.service.ServiceImpl;

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

    /** Must match a pickup address nickname in Shiprocket → Settings → Pickup Addresses. */
    @Value("${shiprocket.pickup-location}")
    private String pickupLocation;

    /** STEP 1: create the order in Shiprocket. Returns Shiprocket's order_id and shipment_id. */
    public OrderResponse createOrder(OrderRequestBody orderRequestBody) {
        if (!StringUtils.hasText(orderRequestBody.getPickup_location())) {
            orderRequestBody.setPickup_location(pickupLocation);
        }
        OrderResponse response = withToken(h -> shiprocketServiceProxy.createShiprocketOrderResponse(h, orderRequestBody));
        log.info("Shiprocket order created for {}: shiprocketOrderId={}, shipmentId={}, status={}",
                orderRequestBody.getOrder_id(),
                response == null ? null : response.getOrderId(),
                response == null ? null : response.getShipmentId(),
                response == null ? null : response.getStatus());
        return response;
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
