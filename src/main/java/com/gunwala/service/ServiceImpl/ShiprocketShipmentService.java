

package com.gunwala.service.ServiceImpl;
import com.gunwala.shipRocket.ShiprocketServiceProxy;
import com.gunwala.shipRocket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ShiprocketShipmentService {

    @Autowired
    private ShiprocketServiceProxy shiprocketServiceProxy;

    @Autowired
    private ShiprocketTokenManager tokenManager;

    @Value("${shiprocket.pickup-location}")
    private String pickupLocation;

    /**
     * STEP 1: Create order in Shiprocket after Razorpay payment success.
     * orderRequestBody must already have all order/item/customer fields set
     * by the caller (OrderController) before calling this.
     */
    public OrderResponse createOrder(OrderRequestBody orderRequestBody) {
        try {
            // Make sure pickup_location matches your Shiprocket dashboard setting
            orderRequestBody.setPickupLocation(pickupLocation); // ← adjust setter name to your DTO

            Map<String, String> headers = tokenManager.authHeaders();
            OrderResponse response = shiprocketServiceProxy.createShiprocketOrderResponse(headers, orderRequestBody);

            System.out.println("✅ Shiprocket order created: " + response); // adjust getter for log
            return response;

        } catch (Exception e) {
            // Token might have expired mid-session — retry once with fresh token
            if (isAuthError(e)) {
                tokenManager.invalidateToken();
                Map<String, String> freshHeaders = tokenManager.authHeaders();
                return shiprocketServiceProxy.createShiprocketOrderResponse(freshHeaders, orderRequestBody);
            }
            throw new RuntimeException("Failed to create Shiprocket order: " + e.getMessage(), e);
        }
    }

    /**
     * STEP 2: Assign AWB (courier + tracking number) to a shipment.
     * Call this right after createOrder() succeeds.
     */
    public AwbResponseAssaign generateAwb(String shipmentId, String courierId) {
        try {
            AwbRequestBody body = new AwbRequestBody();
            body.setShipmentId(shipmentId);   // ← adjust setter names to your DTO
            body.setCourierId(courierId);     // pass "" or null to let Shiprocket auto-assign

            Map<String, String> headers = tokenManager.authHeaders();
            return shiprocketServiceProxy.generateAwb(headers, body);

        } catch (Exception e) {
            if (isAuthError(e)) {
                tokenManager.invalidateToken();
                AwbRequestBody body = new AwbRequestBody();
                body.setShipmentId(shipmentId);
                body.setCourierId(courierId);
                Map<String, String> freshHeaders = tokenManager.authHeaders();
                return shiprocketServiceProxy.generateAwb(freshHeaders, body);
            }
            throw new RuntimeException("Failed to generate AWB: " + e.getMessage(), e);
        }
    }

    /**
     * STEP 3: Request pickup from courier for a given shipment.
     */
    public PickupResponse generatePickup(String shipmentId) {
        try {
            Map<String, String> headers = tokenManager.authHeaders();
            return shiprocketServiceProxy.generatePickup(headers, shipmentId);
        } catch (Exception e) {
            if (isAuthError(e)) {
                tokenManager.invalidateToken();
                Map<String, String> freshHeaders = tokenManager.authHeaders();
                return shiprocketServiceProxy.generatePickup(freshHeaders, shipmentId);
            }
            throw new RuntimeException("Failed to generate pickup: " + e.getMessage(), e);
        }
    }

    /**
     * Track shipment status by Shiprocket shipment ID.
     */
    public TrackingReaponse trackShipment(String shipmentId) {
        try {
            Map<String, String> headers = tokenManager.authHeaders();
            return shiprocketServiceProxy.trackShipment(shipmentId, headers);
        } catch (Exception e) {
            if (isAuthError(e)) {
                tokenManager.invalidateToken();
                Map<String, String> freshHeaders = tokenManager.authHeaders();
                return shiprocketServiceProxy.trackShipment(shipmentId, freshHeaders);
            }
            throw new RuntimeException("Failed to track shipment: " + e.getMessage(), e);
        }
    }

    /**
     * Full automated flow — call this ONE method after Razorpay payment success.
     * It creates the order, assigns AWB, and schedules pickup in sequence.
     */
    public OrderResponse createOrderAndShip(OrderRequestBody orderRequestBody) {
        OrderResponse orderResponse = createOrder(orderRequestBody);

        // ── Extract shipment_id from response — adjust getter to your DTO ────
        String shipmentId = String.valueOf(orderResponse.getShipmentId());

        if (shipmentId != null && !shipmentId.equals("null")) {
            try {
                // "" / null courierId = let Shiprocket auto-pick best courier
                generateAwb(shipmentId, "");
                generatePickup(shipmentId);
            } catch (Exception e) {
                // Order was created successfully even if AWB/pickup fails —
                // log it so admin can retry manually
                System.err.println("⚠️ Order created but AWB/pickup failed: " + e.getMessage());
            }
        }

        return orderResponse;
    }

    private boolean isAuthError(Exception e) {
        String msg = e.getMessage();
        return msg != null && (msg.contains("401") || msg.toLowerCase().contains("unauthorized"));
    }
}