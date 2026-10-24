package com.gunwala.dto;

import com.gunwala.entity.OrderStatus;
import com.gunwala.entity.ShippingStatus;
import com.gunwala.entity.ShopOrder;
import com.gunwala.entity.ShopOrderItem;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ShopOrderResponse(
        String orderReference,
        String userId,
        OrderStatus status,
        BigDecimal subtotal,
        List<Item> items,
        Address shippingAddress,
        Long paidPaymentSubmissionId,
        Instant paidAt,
        Shipping shipping,
        Instant createdAt
) {
    public record Item(Integer productId, String productName, int units, BigDecimal unitPrice,
                       boolean licenceRequired, boolean storeProduct) {
        static Item from(ShopOrderItem i) {
            return new Item(i.getProductId(), i.getProductName(), i.getUnits(), i.getUnitPrice(),
                    i.isLicenceRequired(), i.isStoreProduct());
        }
    }

    public record Address(String fullName, String phone, String email, String addressLine1, String addressLine2,
                          String city, String state, String pincode, String country) {}

    public record Shipping(
            ShippingStatus status,
            String awbCode,
            String courierName,
            String pickupScheduledDate,
            String trackingUrl,
            /* admin view only */ Long shiprocketOrderId,
            /* admin view only */ Long shiprocketShipmentId,
            /* admin view only */ String error,
            /* admin view only */ Integer attempts,
            /* admin view only */ Instant lastAttemptAt
    ) {}

    /** Must be called inside a transaction (items are lazy). */
    public static ShopOrderResponse from(ShopOrder o, boolean adminView) {
        String trackingUrl = StringUtils.hasText(o.getAwbCode())
                ? "https://shiprocket.co/tracking/" + o.getAwbCode()
                : null;
        Shipping shipping = new Shipping(
                o.getShippingStatus(), o.getAwbCode(), o.getCourierName(), o.getPickupScheduledDate(), trackingUrl,
                adminView ? o.getShiprocketOrderId() : null,
                adminView ? o.getShiprocketShipmentId() : null,
                adminView ? o.getShippingError() : null,
                adminView ? o.getShippingAttempts() : null,
                adminView ? o.getLastShippingAttemptAt() : null);
        Address address = new Address(o.getCustomerName(), o.getPhone(), o.getEmail(), o.getAddressLine1(),
                o.getAddressLine2(), o.getCity(), o.getState(), o.getPincode(), o.getCountry());
        return new ShopOrderResponse(
                o.getOrderReference(),
                adminView ? o.getUserId() : null,
                o.getStatus(), o.getSubtotal(),
                o.getItems().stream().map(Item::from).toList(),
                address, o.getPaidPaymentSubmissionId(), o.getPaidAt(), shipping, o.getCreatedAt());
    }
}
