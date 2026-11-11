package com.gunwala.repository;

import com.gunwala.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserIdOrderByCreatedAtAsc(String userId);

    Optional<CartItem> findByUserIdAndProductId(String userId, Integer productId);

    @Modifying
    @Transactional
    void deleteByUserIdAndProductId(String userId, Integer productId);

    @Modifying
    @Transactional
    void deleteByUserId(String userId);
}
