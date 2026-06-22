package com.gunwala.controller;


import com.gunwala.service.ServiceImpl.ShiprocketShipmentService;
import com.gunwala.shipRocket.model.OrderRequestBody;
import com.gunwala.shipRocket.model.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private ShiprocketShipmentService shipmentService;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    // ─── VERIFY Razorpay payment + AUTO-CREATE Shiprocket shipment ───────────
    // POST /api/orders/verify
    // Body: { razorpay_order_id, razorpay_payment_id, razorpay_signature,
    //         customerName, email, phone, address, city, state, pincode,
    //         subTotal, items: [{name, sku, units, selling_price}] }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPaymentAndShip(@RequestBody Map<String, Object> body) {
        try {
            String razorpayOrderId   = (String) body.get("razorpay_order_id");
            String razorpayPaymentId = (String) body.get("razorpay_payment_id");
            String razorpaySignature = (String) body.get("razorpay_signature");

            // ── STEP 1: Verify Razorpay signature ──────────────────────────────
            boolean isValid = verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            if (!isValid) {
                return ResponseEntity.status(400).body(
                        Map.of("status", "ERROR", "message", "Invalid payment signature")
                );
            }

            // ── STEP 2: Build OrderRequestBody using YOUR DTO ───────────────────
            OrderRequestBody orderRequestBody = new OrderRequestBody();

            String orderId = "BW-" + razorpayOrderId.replace("order_", "");
            orderRequestBody.setOrder_id(orderId);                       // ← adjust setter to your DTO
            orderRequestBody.setOrder_date(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );

            orderRequestBody.setBilling_customer_name((String) body.getOrDefault("customerName", "Customer"));
            orderRequestBody.setBilling_email((String) body.getOrDefault("email", ""));
            orderRequestBody.setBilling_phone((String) body.getOrDefault("phone", ""));
            orderRequestBody.setBilling_address((String) body.getOrDefault("address", ""));
            orderRequestBody.setBilling_city((String) body.getOrDefault("city", ""));
            orderRequestBody.setBilling_state((String) body.getOrDefault("state", ""));
            orderRequestBody.setBilling_pincode((String) body.getOrDefault("pincode", ""));
            orderRequestBody.setBilling_country("India");
            orderRequestBody.setShipping_is_billing(true);
            orderRequestBody.setPayment_method("Prepaid"); // already paid via Razorpay

            Object subTotal = body.get("subTotal");
            orderRequestBody.setSub_total(subTotal != null ? Double.parseDouble(subTotal.toString()) : 0);

            // Dimensions — adjust defaults as per your products
            orderRequestBody.setLength(10);
            orderRequestBody.setBreadth(10);
            orderRequestBody.setHeight(10);
            orderRequestBody.setWeight(0.5);

            // Order items
            Object items = body.get("items");
            if (items instanceof List) {
                orderRequestBody.setOrder_items((List<Map<String, Object>>) items);
            }

            // ── STEP 3: Create order + auto AWB + auto pickup ──────────────────
            OrderResponse shipResponse = shipmentService.createOrderAndShip(orderRequestBody);

            // ── STEP 4: Return combined response to frontend ───────────────────
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Payment verified and shipment created");
            response.put("orderId", orderId);
            response.put("razorpayPaymentId", razorpayPaymentId);
            response.put("shipmentResponse", shipResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Order verify/ship error: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(razorpayKeySecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] hash = mac.doFinal(payload.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().equals(signature);
    }
}