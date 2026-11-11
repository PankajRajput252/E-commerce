package com.gunwala.service.Service;

import com.gunwala.dto.CartResponse;
import com.gunwala.entity.CartItem;
import com.gunwala.entity.Discount;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.model.entitities.gunwala.ProductImage;
import com.gunwala.repo.gunwala.ProductImageRepository;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Server-side cart, one per logged-in user. Only store products can be added (marketplace
 * listings are bought by chatting with the seller). Prices are always read from the products
 * table with live sale discounts, so the cart total matches what checkout will charge.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private static final String ACTIVE = "ACTIVE";
    private static final int MAX_UNITS_PER_LINE = 99;
    private static final int MAX_LINES = 50; // same limit as CreateShopOrderRequest

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final PromotionPricingService pricing;
    private final ImageUploadService imageUploadService;

    @Transactional
    public CartResponse get(String userId) {
        return build(userId);
    }

    /** Adds units to the cart (merging with an existing line), capped at the stock left. */
    @Transactional
    public CartResponse add(String userId, Integer productId, int quantity) {
        Product product = buyableProduct(productId);
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (item == null) {
            if (cartRepository.findByUserIdOrderByCreatedAtAsc(userId).size() >= MAX_LINES) {
                throw new BadRequestException("Your cart is full. Please check out or remove some items first.");
            }
            item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(0);
        }
        item.setQuantity(capQuantity(product, item.getQuantity() + quantity));
        cartRepository.save(item);
        return build(userId);
    }

    /** Sets a line's quantity (from the cart page's +/− controls). */
    @Transactional
    public CartResponse setQuantity(String userId, Integer productId, int quantity) {
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " is not in your cart"));
        item.setQuantity(capQuantity(buyableProduct(productId), quantity));
        cartRepository.save(item);
        return build(userId);
    }

    @Transactional
    public CartResponse remove(String userId, Integer productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
        return build(userId);
    }

    @Transactional
    public CartResponse clear(String userId) {
        cartRepository.deleteByUserId(userId);
        return build(userId);
    }

    // ============================================================ helpers

    private Product buyableProduct(Integer productId) {
        if (productId == null) {
            throw new BadRequestException("productId is required");
        }
        Product product = productRepository.findByProductPkIdAndActiveStateCodeFkId(productId, ACTIVE);
        if (product == null) {
            throw new NotFoundException("This product is no longer available");
        }
        if (!product.getIsStoreProduct()) {
            throw new BadRequestException("Marketplace items can't be added to the cart. Please chat with the seller.");
        }
        if (product.getPrice() == null || product.getPrice().signum() <= 0) {
            throw new BadRequestException("\"" + product.getTitle() + "\" can't be bought online. Please contact the seller.");
        }
        if (stockOf(product) <= 0) {
            throw new BadRequestException("\"" + product.getTitle() + "\" is out of stock");
        }
        return product;
    }

    private int capQuantity(Product product, int wanted) {
        return Math.max(1, Math.min(wanted, Math.min(stockOf(product), MAX_UNITS_PER_LINE)));
    }

    /** null / 0 quantity = out of stock (same rule the website uses). */
    private static int stockOf(Product product) {
        Integer q = product.getProductQuantity();
        return q == null ? 0 : q;
    }

    /** Builds the response with live prices; drops lines whose product is gone. */
    private CartResponse build(String userId) {
        List<CartItem> items = cartRepository.findByUserIdOrderByCreatedAtAsc(userId);
        List<Discount> live = pricing.liveDiscounts();
        List<CartResponse.Line> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            Product product = productRepository.findByProductPkIdAndActiveStateCodeFkId(item.getProductId(), ACTIVE);
            if (product == null || !product.getIsStoreProduct()) {
                // Deleted / deactivated since it was added — nothing left to show or buy
                cartRepository.delete(item);
                continue;
            }

            BigDecimal listPrice = product.getPrice() == null
                    ? BigDecimal.ZERO : PromotionPricingService.money(product.getPrice());
            Optional<PromotionPricingService.Sale> sale = pricing.bestSale(product, live);
            BigDecimal unitPrice = sale.map(PromotionPricingService.Sale::salePrice).orElse(listPrice);

            int stock = stockOf(product);
            boolean available = stock > 0 && listPrice.signum() > 0;
            int quantity = item.getQuantity();
            if (available && quantity > stock) {
                // Stock went down since it was added — trim the line instead of failing at checkout
                quantity = stock;
                item.setQuantity(quantity);
                cartRepository.save(item);
            }

            lines.add(new CartResponse.Line(
                    product.getProductPkId(),
                    product.getTitle(),
                    firstImageUrl(product.getProductPkId()),
                    location(product),
                    quantity,
                    unitPrice,
                    sale.isPresent() ? listPrice : null,
                    product.getProductQuantity(),
                    available));

            if (available) {
                totalItems += quantity;
                subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }
        return new CartResponse(lines, totalItems, PromotionPricingService.money(subtotal));
    }

    private String firstImageUrl(int productId) {
        try {
            List<ProductImage> images = productImageRepository
                    .findByActiveStateCodeFkIdAndProductFkId(ACTIVE, productId, PageRequest.of(0, 1));
            if (images.isEmpty() || !StringUtils.hasText(images.get(0).getProductImageId())) {
                return null;
            }
            return imageUploadService.generatePresignedUrl(images.get(0).getProductImageId());
        } catch (RuntimeException e) {
            // A missing photo must not break the whole cart
            log.warn("Could not load image for product {}: {}", productId, e.getMessage());
            return null;
        }
    }

    private static String location(Product product) {
        if (StringUtils.hasText(product.getLocation())) {
            return product.getLocation();
        }
        String joined = String.join(", ", List.of(
                StringUtils.hasText(product.getCity()) ? product.getCity() : "",
                StringUtils.hasText(product.getState()) ? product.getState() : "")).replaceAll("^, |, $", "");
        return joined.isBlank() ? null : joined;
    }
}
