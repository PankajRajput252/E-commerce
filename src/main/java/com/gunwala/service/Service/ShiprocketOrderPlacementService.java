package com.gunwala.service.Service;

import com.gunwala.dto.ShopOrderResponse;
import com.gunwala.entity.OrderStatus;
import com.gunwala.entity.PaymentSubmission;
import com.gunwala.entity.ShippingStatus;
import com.gunwala.entity.ShopOrder;
import com.gunwala.entity.ShopOrderItem;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.repository.PaymentSubmissionRepository;
import com.gunwala.repository.ShopOrderRepository;
import com.gunwala.service.ServiceImpl.ShiprocketShipmentService;
import com.gunwala.shipRocket.model.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Places a paid order with Shiprocket: create order → assign AWB → request pickup.
 *
 * <p>Each step's result is saved before the next call, so if a step fails the order keeps the ids
 * it already has and a retry continues from the failed step instead of creating a duplicate
 * Shiprocket order. Shiprocket is never called inside a database transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShiprocketOrderPlacementService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter SHIPROCKET_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** A PROCESSING order older than this is treated as stuck (e.g. server restarted mid-call). */
    private static final Duration STUCK_AFTER = Duration.ofMinutes(5);

    private final ShopOrderRepository orderRepository;
    private final PaymentSubmissionRepository paymentRepository;
    private final ShiprocketShipmentService shiprocket;
    private final TransactionTemplate tx;

    @Value("${shiprocket.auto-ship.enabled:true}")
    private boolean autoShipEnabled;

    @Value("${shiprocket.auto-ship.assign-awb:true}")
    private boolean assignAwb;

    @Value("${shiprocket.auto-ship.request-pickup:true}")
    private boolean requestPickup;

    /** Items listed by other sellers ship from their address, not ours, so they need manual handling. */
    @Value("${shiprocket.auto-ship.store-products-only:true}")
    private boolean storeProductsOnly;

    @Value("${shiprocket.package.length-cm:30}")
    private int packageLength;
    @Value("${shiprocket.package.breadth-cm:20}")
    private int packageBreadth;
    @Value("${shiprocket.package.height-cm:10}")
    private int packageHeight;
    @Value("${shiprocket.package.weight-kg:1.0}")
    private double packageWeight;

    // ============================================================ entry points

    /** Called (asynchronously) after an admin approves a payment. Never throws. */
    public void onPaymentApproved(Long paymentSubmissionId) {
        String orderReference = paymentRepository.findById(paymentSubmissionId)
                .map(PaymentSubmission::getOrderReference)
                .orElse(null);
        if (!StringUtils.hasText(orderReference)) {
            log.info("Payment {} approved; it isn't linked to an order, so nothing to ship", paymentSubmissionId);
            return;
        }
        if (!autoShipEnabled) {
            log.info("Payment {} approved for order {}; auto-ship is disabled (shiprocket.auto-ship.enabled=false)",
                    paymentSubmissionId, orderReference);
            return;
        }
        try {
            ShopOrderResponse result = placeShipment(orderReference, false);
            log.info("Auto-ship for order {} finished with status {}", orderReference, result.shipping().status());
        } catch (Exception e) {
            // placeShipment already saved FAILED/MANUAL_REVIEW where relevant; this catches claim conflicts etc.
            log.warn("Auto-ship for order {} did not run: {}", orderReference, e.getMessage());
        }
    }

    /**
     * Runs the remaining Shiprocket steps for a paid order. Used by auto-ship and by the admin "Retry" button.
     * Returns the order as saved; a failed step is reported in shipping.status=FAILED and shipping.error.
     */
    public ShopOrderResponse placeShipment(String orderReference, boolean triggeredByAdmin) {
        ShopOrder order = claim(orderReference, triggeredByAdmin);
        if (order == null) {
            return load(orderReference);  // nothing to do (already complete, or sent to manual review)
        }

        try {
            // ---- Step 1: create the Shiprocket order ----
            if (order.getShiprocketOrderId() == null) {
                OrderResponse created = shiprocket.createOrder(buildOrderRequest(order));
                if (created == null || created.getOrderId() == null || created.getShipmentId() == null) {
                    throw new IllegalStateException("Shiprocket did not return an order/shipment id"
                            + (created != null && created.getStatus() != null ? " (status: " + created.getStatus() + ")" : "")
                            + (created != null && StringUtils.hasText(created.getPackagingBoxError()) ? " – " + created.getPackagingBoxError() : ""));
                }
                order = save(orderReference, o -> {
                    o.setShiprocketOrderId(created.getOrderId());
                    o.setShiprocketShipmentId(created.getShipmentId());
                    o.setShippingStatus(ShippingStatus.ORDER_CREATED);
                    if (StringUtils.hasText(created.getAwbCode())) {   // some accounts auto-assign on create
                        o.setAwbCode(created.getAwbCode());
                        o.setCourierName(created.getCourierName());
                        o.setShippingStatus(ShippingStatus.AWB_ASSIGNED);
                    }
                });
            }

            // ---- Step 2: assign courier + AWB ----
            if (assignAwb && !StringUtils.hasText(order.getAwbCode())) {
                AwbResponseAssaign awb = shiprocket.generateAwb(String.valueOf(order.getShiprocketShipmentId()), null);
                AwbResponseData data = awb != null && awb.getResponse() != null ? awb.getResponse().getData() : null;
                if (awb == null || awb.getAwb_assign_status() != 1 || data == null || !StringUtils.hasText(data.getAwb_code())) {
                    throw new IllegalStateException("Shiprocket could not assign a courier/AWB. Check the Shiprocket wallet balance "
                            + "and that a courier serves PIN " + order.getPincode() + ".");
                }
                order = save(orderReference, o -> {
                    o.setAwbCode(data.getAwb_code());
                    o.setCourierName(data.getCourier_name());
                    o.setPickupScheduledDate(data.getPickup_scheduled_date());
                    o.setShippingStatus(ShippingStatus.AWB_ASSIGNED);
                });
            }

            // ---- Step 3: request pickup ----
            if (assignAwb && requestPickup && order.getShippingStatus() != ShippingStatus.PICKUP_SCHEDULED) {
                String scheduled = requestPickup(order.getShiprocketShipmentId());
                order = save(orderReference, o -> {
                    o.setShippingStatus(ShippingStatus.PICKUP_SCHEDULED);
                    if (StringUtils.hasText(scheduled)) o.setPickupScheduledDate(scheduled);
                });
            }

            // ---- Done: settle the final status for whichever steps are enabled ----
            return toResponse(save(orderReference, o -> {
                o.setShippingError(null);
                if (o.getShippingStatus() == ShippingStatus.PROCESSING) {   // e.g. AWB step disabled and nothing new happened
                    o.setShippingStatus(StringUtils.hasText(o.getAwbCode()) ? ShippingStatus.AWB_ASSIGNED : ShippingStatus.ORDER_CREATED);
                }
            }));

        } catch (Exception e) {
            String reason = ShiprocketShipmentService.describe(e);
            log.error("Shiprocket shipping failed for order {}: {}", orderReference, reason);
            return toResponse(save(orderReference, o -> {
                o.setShippingStatus(ShippingStatus.FAILED);
                o.setShippingError(reason);
            }));
        }
    }

    /**
     * Requests pickup and returns the scheduled date (may be null).
     * Many Shiprocket accounts schedule the pickup automatically when the AWB is assigned; Shiprocket then
     * answers 400 "Already in Pickup Queue", which means the pickup IS scheduled, so it counts as success.
     */
    private String requestPickup(Long shipmentId) {
        try {
            PickupResponse pickup = shiprocket.generatePickup(String.valueOf(shipmentId));
            if (pickup == null || pickup.getPickup_status() == null || pickup.getPickup_status() != 1) {
                throw new IllegalStateException("Shiprocket did not accept the pickup request");
            }
            return pickup.getResponse() != null ? pickup.getResponse().getPickup_scheduled_date() : null;
        } catch (FeignException e) {
            String body = e.contentUTF8();
            if (body != null && body.toLowerCase().contains("already in pickup queue")) {
                log.info("Shipment {} was already in Shiprocket's pickup queue (auto-scheduled with the AWB)", shipmentId);
                return null;
            }
            throw e;
        }
    }

    // ============================================================ claim

    /**
     * Marks the order PROCESSING (optimistic lock, so two triggers can't both ship it).
     * Returns null when there is nothing to do.
     */
    private ShopOrder claim(String orderReference, boolean triggeredByAdmin) {
        try {
            return tx.execute(status -> {
                ShopOrder o = orderRepository.findByOrderReference(orderReference)
                        .orElseThrow(() -> new NotFoundException("Order " + orderReference + " not found"));

                if (o.getStatus() != OrderStatus.PAID) {
                    throw new ConflictException("Order " + orderReference + " isn't paid yet, so it can't be shipped");
                }
                String manualReason = manualHandlingReason(o);
                if (manualReason != null) {
                    if (o.getShippingStatus() != ShippingStatus.MANUAL_REVIEW) {
                        o.setShippingStatus(ShippingStatus.MANUAL_REVIEW);
                        o.setShippingError(manualReason);
                        log.info("Order {} needs manual handling: {}", orderReference, manualReason);
                    }
                    if (triggeredByAdmin) {
                        throw new ConflictException(manualReason);
                    }
                    return null;
                }
                if (isComplete(o)) {
                    return null;
                }
                if (o.getShippingStatus() == ShippingStatus.PROCESSING && o.getLastShippingAttemptAt() != null
                        && o.getLastShippingAttemptAt().isAfter(Instant.now().minus(STUCK_AFTER))) {
                    throw new ConflictException("Shipment for order " + orderReference + " is already being created. Try again in a few minutes.");
                }

                o.setShippingStatus(ShippingStatus.PROCESSING);
                o.setShippingAttempts(o.getShippingAttempts() + 1);
                o.setLastShippingAttemptAt(Instant.now());
                o.getItems().size();  // load items for the request body
                return orderRepository.saveAndFlush(o);
            });
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConflictException("Order " + orderReference + " was updated at the same moment. Refresh and try again.");
        }
    }

    private String manualHandlingReason(ShopOrder o) {
        if (o.containsLicenceRequiredItem()) {
            return "Contains licence-required item(s). Not sent to Shiprocket: verify the buyer's licence and arrange a lawful transfer manually.";
        }
        if (storeProductsOnly && o.containsThirdPartySellerItem()) {
            return "Contains item(s) listed by another seller, which ship from the seller's address. Arrange shipping with the seller.";
        }
        return null;
    }

    private boolean isComplete(ShopOrder o) {
        if (o.getShiprocketOrderId() == null) return false;
        if (!assignAwb) return true;
        if (!StringUtils.hasText(o.getAwbCode())) return false;
        return !requestPickup || o.getShippingStatus() == ShippingStatus.PICKUP_SCHEDULED;
    }

    // ============================================================ request body

    private OrderRequestBody buildOrderRequest(ShopOrder o) {
        OrderRequestBody b = new OrderRequestBody();
        b.setOrder_id(o.getOrderReference());
        b.setOrder_date((o.getPaidAt() != null ? o.getPaidAt() : Instant.now()).atZone(IST).format(SHIPROCKET_DATE));
        b.setComment("Bandookwale order " + o.getOrderReference() + " (bank/UPI payment #" + o.getPaidPaymentSubmissionId() + ")");

        String[] name = splitName(o.getCustomerName());
        b.setBilling_customer_name(name[0]);
        b.setBilling_last_name(name[1]);
        b.setBilling_address(o.getAddressLine1());
        b.setBilling_address_2(o.getAddressLine2());
        b.setBilling_city(o.getCity());
        b.setBilling_state(o.getState());
        b.setBilling_pincode(o.getPincode());
        b.setBilling_country(o.getCountry());
        b.setBilling_email(o.getEmail());
        b.setBilling_phone(o.getPhone());
        b.setShipping_is_billing(true);

        List<Map<String, Object>> items = new ArrayList<>();
        for (ShopOrderItem i : o.getItems()) {
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("name", i.getProductName());
            line.put("sku", i.getSku());
            line.put("units", i.getUnits());
            line.put("selling_price", i.getUnitPrice());
            items.add(line);
        }
        b.setOrder_items(items);

        b.setPayment_method("Prepaid");   // already paid by bank transfer / UPI
        b.setShipping_charges(0);
        b.setGiftwrap_charges(0);
        b.setTransaction_charges(0);
        b.setTotal_discount(0);
        b.setSub_total(o.getSubtotal().doubleValue());

        b.setLength(packageLength);
        b.setBreadth(packageBreadth);
        b.setHeight(packageHeight);
        b.setWeight(packageWeight);
        return b;
    }

    /** "Ravi Kumar Singh" → ["Ravi", "Kumar Singh"]; Shiprocket requires a first name. */
    private static String[] splitName(String full) {
        String trimmed = full == null ? "" : full.trim();
        int space = trimmed.indexOf(' ');
        return space < 0 ? new String[]{trimmed, ""} : new String[]{trimmed.substring(0, space), trimmed.substring(space + 1).trim()};
    }

    // ============================================================ persistence helpers

    /** Applies a change in its own short transaction and returns the fresh order (with items loaded). */
    private ShopOrder save(String orderReference, Consumer<ShopOrder> change) {
        return tx.execute(status -> {
            ShopOrder o = orderRepository.findByOrderReference(orderReference)
                    .orElseThrow(() -> new NotFoundException("Order " + orderReference + " not found"));
            change.accept(o);
            o.getItems().size();
            return orderRepository.saveAndFlush(o);
        });
    }

    private ShopOrderResponse load(String orderReference) {
        return tx.execute(status -> toResponse(orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new NotFoundException("Order " + orderReference + " not found"))));
    }

    private ShopOrderResponse toResponse(ShopOrder o) {
        return ShopOrderResponse.from(o, true);
    }
}
