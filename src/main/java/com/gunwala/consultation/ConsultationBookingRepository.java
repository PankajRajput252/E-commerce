package com.gunwala.consultation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface ConsultationBookingRepository extends JpaRepository<ConsultationBooking, Long> {

    List<ConsultationBooking> findByUserIdOrderByCreatedAtDesc(String userId);

    /** A user's bookings with this status starting after {@code now}. */
    List<ConsultationBooking> findByUserIdAndStatusAndStartsAtAfter(String userId, ConsultationBooking.Status status, Instant now);

    List<ConsultationBooking> findByStartsAtBetweenOrderByStartsAtAsc(Instant from, Instant to);

    List<ConsultationBooking> findBySlotId(Long slotId);

    List<ConsultationBooking> findBySlotIdInAndStatus(Collection<Long> slotIds, ConsultationBooking.Status status);
}
