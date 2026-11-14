package com.gunwala.consultation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ConsultationSlotRepository extends JpaRepository<ConsultationSlot, Long> {

    /** Row lock so two users can't book the same slot at the same moment. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ConsultationSlot s where s.id = :id")
    Optional<ConsultationSlot> findByIdForUpdate(@Param("id") Long id);

    List<ConsultationSlot> findByBookedFalseAndStartsAtBetweenOrderByStartsAtAsc(Instant from, Instant to);

    List<ConsultationSlot> findByStartsAtBetweenOrderByStartsAtAsc(Instant from, Instant to);
}
