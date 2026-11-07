package com.gunwala.service.Service;

import com.gunwala.entity.Discount;
import com.gunwala.entity.DiscountScope;
import com.gunwala.entity.PromoValueType;
import com.gunwala.entity.Voucher;
import com.gunwala.model.entitities.gunwala.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Pure price maths — no Spring context, no database. */
class PromotionMathTest {

    private final PromotionPricingService pricing = new PromotionPricingService(null, null);

    private static Product product(int id, String price, boolean store) {
        Product p = new Product();
        p.setProductPkId(id);
        p.setPrice(new BigDecimal(price));
        p.setIsStoreProduct(store);
        return p;
    }

    private static Discount discount(long id, PromoValueType type, String value, DiscountScope scope, Integer... productIds) {
        Discount d = new Discount();
        d.setId(id);
        d.setValueType(type);
        d.setValue(new BigDecimal(value));
        d.setScope(scope);
        d.setProductIds(new java.util.HashSet<>(Set.of(productIds)));
        d.setStartsAt(Instant.now().minusSeconds(60));
        d.setActive(true);
        return d;
    }

    private static Voucher voucher(PromoValueType type, String value, String cap) {
        Voucher v = new Voucher();
        v.setValueType(type);
        v.setValue(new BigDecimal(value));
        v.setMaxDiscountAmount(cap == null ? null : new BigDecimal(cap));
        return v;
    }

    @Test
    void bestOfOverlappingDiscountsWins_notStacked() {
        Product p = product(1, "20000", true);
        var sale = pricing.bestSale(p, List.of(
                discount(1, PromoValueType.PERCENTAGE, "20", DiscountScope.ALL_STORE_PRODUCTS),
                discount(2, PromoValueType.FLAT, "5000", DiscountScope.SELECTED_PRODUCTS, 1)));
        assertEquals(new BigDecimal("15000.00"), sale.orElseThrow().salePrice());
        assertEquals("₹5,000 OFF", sale.get().label());
    }

    @Test
    void flatLargerThanPrice_clampsToZero() {
        var sale = pricing.bestSale(product(1, "300", true),
                List.of(discount(1, PromoValueType.FLAT, "500", DiscountScope.ALL_STORE_PRODUCTS)));
        assertEquals(new BigDecimal("0.00"), sale.orElseThrow().salePrice());
    }

    @Test
    void userListingsAreNeverDiscounted() {
        var sale = pricing.bestSale(product(1, "20000", false),
                List.of(discount(1, PromoValueType.PERCENTAGE, "50", DiscountScope.ALL_STORE_PRODUCTS)));
        assertTrue(sale.isEmpty());
    }

    @Test
    void selectedScopeOnlyCoversListedProducts() {
        var sale = pricing.bestSale(product(2, "1000", true),
                List.of(discount(1, PromoValueType.PERCENTAGE, "10", DiscountScope.SELECTED_PRODUCTS, 1)));
        assertTrue(sale.isEmpty());
    }

    @Test
    void percentageRoundsToPaise() {
        var sale = pricing.bestSale(product(1, "999.99", true),
                List.of(discount(1, PromoValueType.PERCENTAGE, "12.5", DiscountScope.ALL_STORE_PRODUCTS)));
        assertEquals(new BigDecimal("874.99"), sale.orElseThrow().salePrice()); // 999.99 × 0.875 = 874.99125
        assertEquals("12.5% OFF", sale.get().label());
    }

    @Test
    void percentVoucherIsCapped() {
        assertEquals(new BigDecimal("2000.00"),
                VoucherService.discountFor(voucher(PromoValueType.PERCENTAGE, "20", "2000"), new BigDecimal("30000")));
        assertEquals(new BigDecimal("1000.00"),
                VoucherService.discountFor(voucher(PromoValueType.PERCENTAGE, "10", null), new BigDecimal("10000")));
    }

    @Test
    void flatVoucherNeverExceedsSubtotal() {
        assertEquals(new BigDecimal("400.00"),
                VoucherService.discountFor(voucher(PromoValueType.FLAT, "500", null), new BigDecimal("400")));
    }
}
