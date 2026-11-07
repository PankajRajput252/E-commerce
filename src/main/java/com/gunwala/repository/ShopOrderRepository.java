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

import java.time.Instant;
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

    /**
     * Uses of a voucher. An order counts when it is paid or has a payment submitted that wasn't rejected.
     * With includeHolds, unpaid orders created after holdSince count too (a short reservation at checkout),
     * so abandoned checkouts free their use again instead of burning it forever.
     *
     * @param userId     only this buyer's orders; null = everyone
     * @param excludeRef leave this order out (when re-checking the order being paid); null = none
     * @param holdOwner  this buyer's own unpaid orders don't hold a use against them (so they can retry checkout)
     */
    @Query("""
            select count(o) from ShopOrder o
            where o.voucherId = :voucherId
              and o.status <> com.gunwala.entity.OrderStatus.CANCELLED
              and (:userId is null or o.userId = :userId)
              and (:excludeRef is null or o.orderReference <> :excludeRef)
              and (o.status = com.gunwala.entity.OrderStatus.PAID
                   or exists (select p.id from PaymentSubmission p
                              where p.orderReference = o.orderReference
                                and p.status <> com.gunwala.entity.PaymentStatus.REJECTED)
                   or (:includeHolds = true and o.createdAt > :holdSince
                       and (:holdOwner is null or o.userId <> :holdOwner)))
            """)
    long countVoucherUses(@Param("voucherId") Long voucherId,
                          @Param("userId") String userId,
                          @Param("excludeRef") String excludeRef,
                          @Param("includeHolds") boolean includeHolds,
                          @Param("holdSince") Instant holdSince,
                          @Param("holdOwner") String holdOwner);
}
