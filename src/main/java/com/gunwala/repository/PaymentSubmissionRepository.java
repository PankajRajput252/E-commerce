package com.gunwala.repository;

import com.gunwala.entity.PaymentStatus;
import com.gunwala.entity.PaymentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
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

    /** Stops a second payment for the same order while one is still waiting for review. */
    boolean existsByOrderReferenceAndStatus(String orderReference, PaymentStatus status);

    boolean existsByOrderReference(String orderReference);

    /** Stops a second payment for the same consultation booking while one is waiting for review or approved. */
    boolean existsByConsultationBookingIdAndStatusIn(Long consultationBookingId, Collection<PaymentStatus> statuses);

    /** Payments for these consultation bookings, newest first (the first per booking is its current one). */
    List<PaymentSubmission> findByConsultationBookingIdInOrderByCreatedAtDesc(Collection<Long> consultationBookingIds);

    /** Finds a live (non-rejected) payment using this reference; a rejected one may be submitted again. */
    Optional<PaymentSubmission> findFirstByTransactionReferenceAndStatusNotOrderByCreatedAtDesc(
            String transactionReference, PaymentStatus status);
}
