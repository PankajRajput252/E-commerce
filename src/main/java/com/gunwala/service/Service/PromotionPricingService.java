package com.gunwala.service.Service;

import com.gunwala.dto.CreateShopOrderRequest;
import com.gunwala.entity.Discount;
import com.gunwala.entity.PromoValueType;
import com.gunwala.exception.BadRequestException;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;

/**
 * Server-side prices: applies live sale discounts to products and prices a cart.
 * The browser only ever sends product ids and quantities.
 */
@Service
@RequiredArgsConstructor
public class PromotionPricingService {

    private static final String ACTIVE = "ACTIVE";
    private static final int MAX_UNITS_PER_LINE = 99;

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    /** Sale on one product: the discounted unit price and a label such as "15% OFF". */
    public record Sale(BigDecimal salePrice, String label, Long discountId) {}

    /** One priced cart line. originalUnitPrice is null when no sale applied. */
    public record PricedLine(Product product, int units, BigDecimal unitPrice, BigDecimal originalUnitPrice) {
        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(units));
        }
    }

    public record PricedCart(List<PricedLine> lines, BigDecimal subtotal) {}

    // ============================================================ sale discounts

    @Transactional(readOnly = true)
    public List<Discount> liveDiscounts() {
        return discountRepository.findLive(Instant.now());
    }

    /** Best (lowest-price) live sale for a product, or empty. Only store products are ever discounted. */
    public Optional<Sale> bestSale(Product product, List<Discount> live) {
        if (product == null || !product.getIsStoreProduct()) return Optional.empty();
        BigDecimal price = product.getPrice();
        if (price == null || price.signum() <= 0) return Optional.empty();

        Sale best = null;
        for (Discount d : live) {
            if (!d.covers(product.getProductPkId())) continue;
            BigDecimal candidate = money(applyValue(price, d.getValueType(), d.getValue()).max(BigDecimal.ZERO));
            if (candidate.compareTo(price) < 0 && (best == null || candidate.compareTo(best.salePrice()) < 0)) {
                best = new Sale(candidate, label(d.getValueType(), d.getValue()), d.getId());
            }
        }
        return Optional.ofNullable(best);
    }

    /** Fills Product.salePrice / discountLabel for API responses. Null-safe. */
    @Transactional(readOnly = true)
    public void applySalePrices(Collection<Product> products) {
        if (products == null || products.isEmpty()) return;
        List<Discount> live = liveDiscounts();
        for (Product p : products) {
            if (p == null) continue;
            Optional<Sale> sale = live.isEmpty() ? Optional.empty() : bestSale(p, live);
            p.setSalePrice(sale.map(Sale::salePrice).orElse(null));
            p.setDiscountLabel(sale.map(Sale::label).orElse(null));
        }
    }

    // ============================================================ cart pricing

    /** Prices cart lines from the products table, with sale discounts applied. Merges duplicate product lines. */
    @Transactional(readOnly = true)
    public PricedCart priceCart(List<CreateShopOrderRequest.Item> items) {
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        for (CreateShopOrderRequest.Item item : items) {
            quantities.merge(item.productId(), item.quantity(), Integer::sum);
        }

        List<Discount> live = liveDiscounts();
        List<PricedLine> lines = new ArrayList<>();
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
            int units = Math.min(line.getValue(), MAX_UNITS_PER_LINE);
            BigDecimal listPrice = money(price);
            Optional<Sale> sale = bestSale(product, live);

            PricedLine priced = new PricedLine(product, units,
                    sale.map(Sale::salePrice).orElse(listPrice),
                    sale.isPresent() ? listPrice : null);
            lines.add(priced);
            subtotal = subtotal.add(priced.lineTotal());
        }
        return new PricedCart(lines, money(subtotal));
    }

    // ============================================================ helpers

    /** price − value% or price − ₹value (may be negative; callers clamp). */
    static BigDecimal applyValue(BigDecimal price, PromoValueType type, BigDecimal value) {
        return type == PromoValueType.PERCENTAGE
                ? price.subtract(price.multiply(value).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                : price.subtract(value);
    }

    public static BigDecimal money(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    /** "15% OFF" / "₹3,000 OFF" */
    public static String label(PromoValueType type, BigDecimal value) {
        if (type == PromoValueType.PERCENTAGE) {
            return value.stripTrailingZeros().toPlainString() + "% OFF";
        }
        NumberFormat inr = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"));
        inr.setMaximumFractionDigits(2);
        return "₹" + inr.format(value) + " OFF";
    }
}
