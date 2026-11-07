package com.gunwala.service.Service;

import com.gunwala.dto.DiscountRequest;
import com.gunwala.dto.DiscountResponse;
import com.gunwala.entity.Discount;
import com.gunwala.entity.DiscountScope;
import com.gunwala.entity.PromoValueType;
import com.gunwala.exception.FieldValidationException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Admin management of automatic sale discounts. Orders store their own prices, so edits never change past orders. */
@Service
@RequiredArgsConstructor
public class DiscountService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<DiscountResponse> listAll() {
        return discountRepository.findAllByOrderByCreatedAtDesc().stream().map(DiscountResponse::from).toList();
    }

    @Transactional
    public DiscountResponse create(DiscountRequest request) {
        Discount d = new Discount();
        apply(d, request);
        return DiscountResponse.from(discountRepository.save(d));
    }

    @Transactional
    public DiscountResponse update(Long id, DiscountRequest request) {
        Discount d = find(id);
        apply(d, request);
        return DiscountResponse.from(discountRepository.saveAndFlush(d));
    }

    @Transactional
    public DiscountResponse setActive(Long id, boolean active) {
        Discount d = find(id);
        d.setActive(active);
        return DiscountResponse.from(discountRepository.saveAndFlush(d));
    }

    @Transactional
    public void delete(Long id) {
        discountRepository.delete(find(id));
    }

    // ============================================================ helpers

    private Discount find(Long id) {
        return discountRepository.findById(id).orElseThrow(() -> new NotFoundException("Discount " + id + " not found"));
    }

    private void apply(Discount d, DiscountRequest r) {
        if (r.valueType() == PromoValueType.PERCENTAGE && r.value().compareTo(HUNDRED) >= 0) {
            throw new FieldValidationException("value", "Percentage must be below 100.");
        }
        if (r.endsAt() != null && !r.endsAt().isAfter(r.startsAt())) {
            throw new FieldValidationException("endsAt", "End must be after the start.");
        }

        Set<Integer> productIds = new HashSet<>();
        if (r.scope() == DiscountScope.SELECTED_PRODUCTS) {
            if (r.productIds() == null || r.productIds().isEmpty()) {
                throw new FieldValidationException("productIds", "Pick at least one product.");
            }
            productIds.addAll(r.productIds());
            List<Product> found = productRepository.findAllById(productIds);
            Set<Integer> storeIds = found.stream()
                    .filter(Product::getIsStoreProduct)
                    .map(Product::getProductPkId)
                    .collect(Collectors.toSet());
            Set<Integer> invalid = new HashSet<>(productIds);
            invalid.removeAll(storeIds);
            if (!invalid.isEmpty()) {
                throw new FieldValidationException("productIds",
                        "Only store products can be discounted. Not allowed: " + invalid.stream().sorted().toList());
            }
        }

        d.setName(r.name().trim());
        d.setValueType(r.valueType());
        d.setValue(PromotionPricingService.money(r.value()));
        d.setScope(r.scope());
        d.getProductIds().clear();
        d.getProductIds().addAll(productIds);
        d.setStartsAt(r.startsAt());
        d.setEndsAt(r.endsAt());
        d.setActive(r.active());
    }
}
