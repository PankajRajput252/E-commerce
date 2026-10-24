package com.gunwala.service.Service;

import com.gunwala.dto.CreateShopOrderRequest;
import com.gunwala.dto.PageResponse;
import com.gunwala.dto.ShopOrderResponse;
import com.gunwala.entity.OrderStatus;
import com.gunwala.entity.ShippingStatus;
import com.gunwala.entity.ShopOrder;
import com.gunwala.entity.ShopOrderItem;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.repository.PaymentSubmissionRepository;
import com.gunwala.repository.ShopOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopOrderService {

    private static final String ACTIVE = "ACTIVE";
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final String REF_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShopOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentSubmissionRepository paymentRepository;

    // ============================================================ BUYER

    /** Checkout: prices are taken from the products table, never from the browser. */
    @Transactional
    public ShopOrderResponse create(String userId, CreateShopOrderRequest request) {
        // Merge duplicate lines for the same product
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        for (CreateShopOrderRequest.Item item : request.items()) {
            quantities.merge(item.productId(), item.quantity(), Integer::sum);
        }

        ShopOrder order = new ShopOrder();
        order.setOrderReference(newOrderReference());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingStatus(ShippingStatus.NOT_STARTED);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> line : quantities.entrySet()) {
            Product product = productRepository.findByProductPkIdAndActiveStateCodeFkId(line.getKey(), ACTIVE);
            if (product == null) {
                throw new BadRequestException("Product " + line.getKey() + " is no longer available. Please remove it from your cart.");
            }
            BigDecimal price = product.getPrice();
            if (price == null || price.signum() <= 0) {
                throw new BadRequestException("\"" + product.getTitle() + "\" can't be bought online. Please contact the seller.");
            }
            int units = Math.min(line.getValue(), 99);

            ShopOrderItem item = new ShopOrderItem();
            item.setProductId(product.getProductPkId());
            item.setProductName(truncate(product.getTitle(), 255));
            item.setUnits(units);
            item.setUnitPrice(price.setScale(2, RoundingMode.HALF_UP));
            item.setLicenceRequired(Boolean.TRUE.equals(product.getIsLicenseRequired()));
            item.setStoreProduct(product.getIsStoreProduct());
            order.addItem(item);

            subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(units)));
        }
        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));

        CreateShopOrderRequest.ShippingAddress a = request.shipping();
        order.setCustomerName(a.fullName().trim());
        order.setPhone(normalisePhone(a.phone()));
        order.setEmail(StringUtils.hasText(a.email()) ? a.email().trim() : null);
        order.setAddressLine1(a.addressLine1().trim());
        order.setAddressLine2(StringUtils.hasText(a.addressLine2()) ? a.addressLine2().trim() : null);
        order.setCity(a.city().trim());
        order.setState(a.state().trim());
        order.setPincode(a.pincode().trim());
        order.setCountry("India");

        orderRepository.save(order);
        return ShopOrderResponse.from(order, false);
    }

    /** The buyer's orders. Unpaid checkouts are listed only if a payment was submitted for them. */
    @Transactional(readOnly = true)
    public List<ShopOrderResponse> listForUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(o -> o.getStatus() != OrderStatus.PENDING_PAYMENT
                        || paymentRepository.existsByOrderReference(o.getOrderReference()))
                .map(o -> ShopOrderResponse.from(o, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public ShopOrderResponse getForUser(String orderReference, String userId) {
        return ShopOrderResponse.from(orderRepository.findByOrderReferenceAndUserId(orderReference, userId)
                .orElseThrow(() -> new NotFoundException("Order " + orderReference + " not found")), false);
    }

    // ============================================================ PAYMENT HOOKS

    /**
     * Called when a buyer submits a payment for an order: the order must be theirs, unpaid,
     * and the amount must equal the order total.
     */
    @Transactional(readOnly = true)
    public void validatePayable(String orderReference, String userId, BigDecimal amount) {
        ShopOrder order = orderRepository.findByOrderReferenceAndUserId(orderReference, userId)
                .orElseThrow(() -> new BadRequestException("Order " + orderReference + " not found"));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new ConflictException("Order " + orderReference + " is already paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order " + orderReference + " was cancelled");
        }
        if (amount == null || order.getSubtotal().compareTo(amount) != 0) {
            throw new BadRequestException("Amount must be ₹" + order.getSubtotal().toPlainString() + ", the total of order " + orderReference);
        }
    }

    /** Runs inside the admin's approve transaction, so "payment approved" and "order paid" commit together. */
    @Transactional
    public void markPaid(String orderReference, Long paymentSubmissionId) {
        ShopOrder order = orderRepository.findByOrderReference(orderReference).orElse(null);
        if (order == null) {
            log.warn("Approved payment {} references unknown order {}", paymentSubmissionId, orderReference);
            return;
        }
        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("Order {} was already paid by payment {}; payment {} approved too — check for a double payment",
                    orderReference, order.getPaidPaymentSubmissionId(), paymentSubmissionId);
            return;
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidPaymentSubmissionId(paymentSubmissionId);
        order.setPaidAt(Instant.now());
    }

    // ============================================================ ADMIN

    @Transactional(readOnly = true)
    public PageResponse<ShopOrderResponse> search(OrderStatus status, ShippingStatus shippingStatus, Pageable pageable) {
        return PageResponse.of(orderRepository.search(status, shippingStatus, pageable)
                .map(o -> ShopOrderResponse.from(o, true)));
    }

    @Transactional(readOnly = true)
    public ShopOrderResponse getForAdmin(String orderReference) {
        return ShopOrderResponse.from(orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new NotFoundException("Order " + orderReference + " not found")), true);
    }

    // ============================================================ helpers

    /** e.g. BW20261024-7F3K9Q (unique, short, unambiguous characters). */
    private String newOrderReference() {
        String date = LocalDate.now(IST).format(DateTimeFormatter.BASIC_ISO_DATE);
        for (int attempt = 0; attempt < 5; attempt++) {
            StringBuilder sb = new StringBuilder("BW").append(date).append('-');
            for (int i = 0; i < 6; i++) {
                sb.append(REF_ALPHABET.charAt(RANDOM.nextInt(REF_ALPHABET.length())));
            }
            String ref = sb.toString();
            if (!orderRepository.existsByOrderReference(ref)) {
                return ref;
            }
        }
        throw new IllegalStateException("Could not generate a unique order reference");
    }

    /** Shiprocket wants a plain 10-digit mobile number. */
    static String normalisePhone(String phone) {
        String digits = phone.replaceAll("\\D", "");
        return digits.length() > 10 ? digits.substring(digits.length() - 10) : digits;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "Item";
        return s.length() <= max ? s : s.substring(0, max);
    }
}
