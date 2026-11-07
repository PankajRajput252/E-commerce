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
import com.gunwala.repository.PaymentSubmissionRepository;
import com.gunwala.repository.ShopOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopOrderService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final String REF_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShopOrderRepository orderRepository;
    private final PaymentSubmissionRepository paymentRepository;
    private final PromotionPricingService pricing;
    private final VoucherService voucherService;

    // ============================================================ BUYER

    /**
     * Checkout: prices are taken from the products table (with live sale discounts), never from the browser.
     * An optional voucher is re-checked under a row lock and its amount copied onto the order.
     */
    @Transactional
    public ShopOrderResponse create(String userId, CreateShopOrderRequest request) {
        PromotionPricingService.PricedCart cart = pricing.priceCart(request.items());

        ShopOrder order = new ShopOrder();
        order.setOrderReference(newOrderReference());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingStatus(ShippingStatus.NOT_STARTED);

        for (PromotionPricingService.PricedLine line : cart.lines()) {
            ShopOrderItem item = new ShopOrderItem();
            item.setProductId(line.product().getProductPkId());
            item.setProductName(truncate(line.product().getTitle(), 255));
            item.setUnits(line.units());
            item.setUnitPrice(line.unitPrice());
            item.setOriginalUnitPrice(line.originalUnitPrice());
            item.setLicenceRequired(Boolean.TRUE.equals(line.product().getIsLicenseRequired()));
            item.setStoreProduct(line.product().getIsStoreProduct());
            order.addItem(item);
        }
        order.setSubtotal(cart.subtotal());

        if (StringUtils.hasText(request.voucherCode())) {
            VoucherService.Applied applied = voucherService.applyToNewOrder(request.voucherCode(), userId, cart.subtotal());
            order.setVoucherId(applied.voucher().getId());
            order.setVoucherCode(applied.voucher().getCode());
            order.setVoucherDiscount(applied.discountAmount());
        }

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
     * the amount must equal the order total (after the voucher), and the voucher must still be within its limits.
     */
    @Transactional
    public void validatePayable(String orderReference, String userId, BigDecimal amount) {
        ShopOrder order = orderRepository.findByOrderReferenceAndUserId(orderReference, userId)
                .orElseThrow(() -> new BadRequestException("Order " + orderReference + " not found"));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new ConflictException("Order " + orderReference + " is already paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order " + orderReference + " was cancelled");
        }
        BigDecimal payable = order.getPayableTotal();
        if (amount == null || payable.compareTo(amount) != 0) {
            throw new BadRequestException("Amount must be ₹" + payable.toPlainString() + ", the total of order " + orderReference);
        }
        voucherService.recheckForPayment(order);
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
