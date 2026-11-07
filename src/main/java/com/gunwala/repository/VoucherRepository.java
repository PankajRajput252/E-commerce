package com.gunwala.repository;

import com.gunwala.entity.Voucher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    List<Voucher> findAllByOrderByCreatedAtDesc();

    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    /** Row lock so two checkouts can't both take the last remaining use. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Voucher v where v.code = :code")
    Optional<Voucher> lockByCode(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Voucher v where v.id = :id")
    Optional<Voucher> lockById(@Param("id") Long id);
}
