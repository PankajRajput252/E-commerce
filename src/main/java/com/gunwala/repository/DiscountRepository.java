package com.gunwala.repository;

import com.gunwala.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findAllByOrderByCreatedAtDesc();

    @Query("""
            select d from Discount d
            where d.active = true and d.startsAt <= :now and (d.endsAt is null or d.endsAt > :now)
            """)
    List<Discount> findLive(@Param("now") Instant now);
}
