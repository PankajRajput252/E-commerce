package com.gunwala.service.Service;

import com.gunwala.dto.ValidateVoucherRequest;
import com.gunwala.dto.VoucherQuoteResponse;
import com.gunwala.dto.VoucherRequest;
import com.gunwala.dto.VoucherResponse;
import com.gunwala.entity.PromoValueType;
import com.gunwala.entity.ShopOrder;
import com.gunwala.entity.Voucher;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.FieldValidationException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.repository.ShopOrderRepository;
import com.gunwala.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import static com.gunwala.service.Service.PromotionPricingService.money;

/**
 * Voucher (coupon) codes.
 *
 * <p>How uses are counted: an order uses a voucher once it is paid or has a payment submitted that wasn't
 * rejected. Unpaid checkouts hold a use for {@link #CHECKOUT_HOLD} so the last use can't be handed to two
 * buyers at once, then release it — there is no order-cancel flow, so abandoned checkouts must not burn uses.
 * Limits are checked at checkout and again, under a row lock, when the payment is submitted.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {

    static final Duration CHECKOUT_HOLD = Duration.ofHours(2);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final VoucherRepository voucherRepository;
    private final ShopOrderRepository orderRepository;
    private final PromotionPricingService pricing;

    /** Voucher applied to a new order. */
    public record Applied(Voucher voucher, BigDecimal discountAmount) {}

    // ============================================================ ADMIN

    @Transactional(readOnly = true)
    public List<VoucherResponse> listAll() {
        return voucherRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(v -> VoucherResponse.from(v, usedCount(v)))
                .toList();
    }

    @Transactional
    public VoucherResponse create(VoucherRequest request) {
        String code = normalise(request.code());
        if (voucherRepository.existsByCode(code)) {
            throw new FieldValidationException("code", "Code " + code + " already exists.");
        }
        Voucher v = new Voucher();
        apply(v, request, code);
        v = voucherRepository.save(v);
        return VoucherResponse.from(v, 0);
    }

    @Transactional
    public VoucherResponse update(Long id, VoucherRequest request) {
        Voucher v = find(id);
        String code = normalise(request.code());
        if (voucherRepository.existsByCodeAndIdNot(code, id)) {
            throw new FieldValidationException("code", "Code " + code + " already exists.");
        }
        apply(v, request, code);
        v = voucherRepository.saveAndFlush(v);
        return VoucherResponse.from(v, usedCount(v));
    }

    @Transactional
    public VoucherResponse setActive(Long id, boolean active) {
        Voucher v = find(id);
        v.setActive(active);
        v = voucherRepository.saveAndFlush(v);
        return VoucherResponse.from(v, usedCount(v));
    }

    /** Orders keep their copied code and amount, so deleting never changes what was charged. */
    @Transactional
    public void delete(Long id) {
        voucherRepository.delete(find(id));
    }

    // ============================================================ BUYER

    /** Cart check. Does not reserve anything — the cart calls it again whenever it changes. */
    @Transactional(readOnly = true)
    public VoucherQuoteResponse quote(String userId, ValidateVoucherRequest request) {
        BigDecimal subtotal = pricing.priceCart(request.items()).subtotal();
        Voucher v = voucherRepository.findByCode(normalise(request.code()))
                .orElseThrow(() -> new BadRequestException("This voucher code isn't valid."));
        checkUsable(v, userId, subtotal, null, true);
        BigDecimal amount = discountFor(v, subtotal);
        return new VoucherQuoteResponse(v.getCode(), v.getDescription(), subtotal, amount, money(subtotal.subtract(amount)));
    }

    /** Checkout: lock the voucher row, re-check every rule, return what it takes off. Runs in the order's transaction. */
    @Transactional
    public Applied applyToNewOrder(String code, String userId, BigDecimal subtotal) {
        Voucher v = voucherRepository.lockByCode(normalise(code))
                .orElseThrow(() -> new BadRequestException("This voucher code isn't valid."));
        checkUsable(v, userId, subtotal, null, true);
        return new Applied(v, discountFor(v, subtotal));
    }

    /**
     * Payment submitted for an order that used a voucher: re-check the limits (not the dates — the buyer keeps
     * the price they were shown at checkout). Stops one buyer paying several orders with a once-per-buyer code.
     */
    @Transactional
    public void recheckForPayment(ShopOrder order) {
        if (order.getVoucherId() == null) return;
        Voucher v = voucherRepository.lockById(order.getVoucherId()).orElse(null);
        if (v == null) {
            log.info("Voucher {} on order {} was deleted after checkout; honouring the discount",
                    order.getVoucherCode(), order.getOrderReference());
            return;
        }
        String ref = order.getOrderReference();
        if (v.getUsageLimit() != null
                && orderRepository.countVoucherUses(v.getId(), null, ref, false, Instant.now(), null) >= v.getUsageLimit()) {
            throw new BadRequestException("Voucher " + v.getCode() + " has been fully used since you checked out. "
                    + "Please place the order again from your cart.");
        }
        if (v.getPerUserLimit() != null
                && orderRepository.countVoucherUses(v.getId(), order.getUserId(), ref, false, Instant.now(), null) >= v.getPerUserLimit()) {
            throw new BadRequestException("You've already used voucher " + v.getCode() + " on another order. "
                    + "Please place this order again without it.");
        }
    }

    // ============================================================ rules

    private void checkUsable(Voucher v, String userId, BigDecimal subtotal, String excludeRef, boolean includeHolds) {
        Instant now = Instant.now();
        if (!v.isLiveAt(now)) {
            throw new BadRequestException("This voucher isn't active right now.");
        }
        if (v.getUsageLimit() != null && orderRepository.countVoucherUses(
                v.getId(), null, excludeRef, includeHolds, now.minus(CHECKOUT_HOLD), userId) >= v.getUsageLimit()) {
            throw new BadRequestException("This voucher has been fully used.");
        }
        // Per buyer: only paid / payment-submitted orders count, so retrying an abandoned checkout still works.
        if (v.getPerUserLimit() != null && orderRepository.countVoucherUses(
                v.getId(), userId, excludeRef, false, now, null) >= v.getPerUserLimit()) {
            throw new BadRequestException("You've already used this voucher.");
        }
        if (v.getMinOrderAmount() != null && subtotal.compareTo(v.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Add " + inr(v.getMinOrderAmount().subtract(subtotal))
                    + " more to use this voucher (minimum order " + inr(v.getMinOrderAmount()) + ").");
        }
    }

    /** % of subtotal (capped) or flat ₹, never more than the subtotal. */
    static BigDecimal discountFor(Voucher v, BigDecimal subtotal) {
        BigDecimal amount = v.getValueType() == PromoValueType.PERCENTAGE
                ? subtotal.multiply(v.getValue()).divide(HUNDRED, 4, RoundingMode.HALF_UP)
                : v.getValue();
        if (v.getValueType() == PromoValueType.PERCENTAGE && v.getMaxDiscountAmount() != null) {
            amount = amount.min(v.getMaxDiscountAmount());
        }
        return money(amount.min(subtotal).max(BigDecimal.ZERO));
    }

    private long usedCount(Voucher v) {
        return orderRepository.countVoucherUses(v.getId(), null, null, false, Instant.now(), null);
    }

    private Voucher find(Long id) {
        return voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher " + id + " not found"));
    }

    private void apply(Voucher v, VoucherRequest r, String code) {
        if (r.valueType() == PromoValueType.PERCENTAGE && r.value().compareTo(HUNDRED) >= 0) {
            throw new FieldValidationException("value", "Percentage must be below 100.");
        }
        if (r.valueType() == PromoValueType.FLAT && r.minOrderAmount() != null
                && r.minOrderAmount().compareTo(r.value()) < 0) {
            throw new FieldValidationException("minOrderAmount", "Should be at least the voucher amount.");
        }
        if (r.endsAt() != null && !r.endsAt().isAfter(r.startsAt())) {
            throw new FieldValidationException("endsAt", "End must be after the start.");
        }
        v.setCode(code);
        v.setDescription(r.description() == null || r.description().isBlank() ? null : r.description().trim());
        v.setValueType(r.valueType());
        v.setValue(money(r.value()));
        v.setMaxDiscountAmount(r.valueType() == PromoValueType.PERCENTAGE && r.maxDiscountAmount() != null
                ? money(r.maxDiscountAmount()) : null);
        v.setMinOrderAmount(r.minOrderAmount() == null || r.minOrderAmount().signum() == 0 ? null : money(r.minOrderAmount()));
        v.setUsageLimit(r.usageLimit());
        v.setPerUserLimit(r.perUserLimit());
        v.setStartsAt(r.startsAt());
        v.setEndsAt(r.endsAt());
        v.setActive(r.active());
    }

    private static String normalise(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private static String inr(BigDecimal v) {
        NumberFormat f = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"));
        f.setMaximumFractionDigits(2);
        return "₹" + f.format(v);
    }
}
