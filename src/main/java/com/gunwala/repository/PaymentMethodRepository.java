package com.gunwala.repository;


import com.gunwala.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByActiveTrueOrderByIdAsc();

    Optional<PaymentMethod> findByIdAndActiveTrue(Long id);
}
