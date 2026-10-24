package com.gunwala.repository;

import com.gunwala.entity.OrderStatus;
import com.gunwala.entity.ShippingStatus;
import com.gunwala.entity.ShopOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<ShopOrder> findByOrderReference(String orderReference);

    @EntityGraph(attributePaths = "items")
    Optional<ShopOrder> findByOrderReferenceAndUserId(String orderReference, String userId);

    @EntityGraph(attributePaths = "items")
    List<ShopOrder> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("""
            select o from ShopOrder o
            where (:status is null or o.status = :status)
              and (:shippingStatus is null or o.shippingStatus = :shippingStatus)
            """)
    Page<ShopOrder> search(@Param("status") OrderStatus status,
                           @Param("shippingStatus") ShippingStatus shippingStatus,
                           Pageable pageable);

    boolean existsByOrderReference(String orderReference);
}
