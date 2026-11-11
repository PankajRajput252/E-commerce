package com.gunwala.repository;

import com.gunwala.entity.SiteVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SiteVisitRepository extends JpaRepository<SiteVisit, Long> {

    Optional<SiteVisit> findByVisitorIdAndVisitDate(String visitorId, LocalDate visitDate);

    /** Distinct visitors, total visits and distinct logged-in users between two dates (inclusive). */
    @Query("""
            select count(distinct v.visitorId), coalesce(sum(v.visits), 0),
                   count(distinct v.userId)
            from SiteVisit v where v.visitDate between :from and :to
            """)
    List<Object[]> totalsBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /** Per day: [date, visitors, visits]. */
    @Query("""
            select v.visitDate, count(v), sum(v.visits)
            from SiteVisit v where v.visitDate >= :from
            group by v.visitDate order by v.visitDate
            """)
    List<Object[]> dailySince(@Param("from") LocalDate from);
}
