package com.gunwala.repository;

import com.gunwala.entity.PaymentStatus;
import com.gunwala.entity.PaymentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentSubmissionRepository extends JpaRepository<PaymentSubmission, Long> {

    @EntityGraph(attributePaths = "paymentMethod")
    List<PaymentSubmission> findByUserIdOrderByCreatedAtDesc(String userId);

    @EntityGraph(attributePaths = "paymentMethod")
    Optional<PaymentSubmission> findByIdAndUserId(Long id, String userId);

    @EntityGraph(attributePaths = "paymentMethod")
    Page<PaymentSubmission> findByStatus(PaymentStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "paymentMethod")
    Page<PaymentSubmission> findAll(Pageable pageable);

    /** Blocks the same UTR being submitted twice (a rejected one may be submitted again). */
    boolean existsByTransactionReferenceAndStatusNot(String transactionReference, PaymentStatus status);
}
