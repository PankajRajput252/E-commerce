package com.gunwala.consultation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConsultationBookingRepository extends JpaRepository<ConsultationBooking, Long> {

    List<ConsultationBooking> findByUserIdOrderByCreatedAtDesc(String userId);

    /** A user's bookings in these statuses starting after {@code now}. */
    List<ConsultationBooking> findByUserIdAndStatusInAndStartsAtAfter(String userId, Collection<ConsultationBooking.Status> statuses, Instant now);

    /** Row lock: serialises payment submission, approval and hold expiry on the same booking. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from ConsultationBooking b where b.id = :id")
    Optional<ConsultationBooking> findByIdForUpdate(@Param("id") Long id);

    /** Unpaid holds whose payment deadline has passed. */
    List<ConsultationBooking> findByStatusAndPaymentDueAtBefore(ConsultationBooking.Status status, Instant now);

    List<ConsultationBooking> findByStartsAtBetweenOrderByStartsAtAsc(Instant from, Instant to);

    List<ConsultationBooking> findBySlotId(Long slotId);

    List<ConsultationBooking> findBySlotIdInAndStatusIn(Collection<Long> slotIds, Collection<ConsultationBooking.Status> statuses);
}
