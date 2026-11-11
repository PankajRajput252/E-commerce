package com.gunwala.service.Service;

import com.gunwala.entity.SiteVisit;
import com.gunwala.exception.BadRequestException;
import com.gunwala.repository.SiteVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

/** Website visitor counts for the admin dashboard. Days are Indian calendar days. */
@Service
@RequiredArgsConstructor
public class SiteVisitService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final Pattern VISITOR_ID = Pattern.compile("^[A-Za-z0-9-]{8,64}$");
    private static final LocalDate BEGINNING = LocalDate.of(2000, 1, 1);

    private final SiteVisitRepository repository;

    public record Totals(long visitors, long visits, long loggedInVisitors) {}

    public record Day(LocalDate date, long visitors, long visits) {}

    public record Stats(Totals today, Totals yesterday, Totals last7Days, Totals last30Days, Totals allTime,
                        List<Day> daily) {}

    /** Counts one browser session. Called once per session by the frontend; guests included. */
    public void record(String visitorId, String userId) {
        if (visitorId == null || !VISITOR_ID.matcher(visitorId).matches()) {
            throw new BadRequestException("Invalid visitor id");
        }
        String user = userId == null || userId.isBlank() ? null : userId.trim();
        if (user != null && user.length() > 100) user = null;
        LocalDate today = LocalDate.now(IST);

        if (bump(visitorId, today, user)) return;
        try {
            SiteVisit v = new SiteVisit();
            v.setVisitorId(visitorId);
            v.setVisitDate(today);
            v.setUserId(user);
            v.setVisits(1);
            v.setFirstSeenAt(Instant.now());
            v.setLastSeenAt(Instant.now());
            repository.saveAndFlush(v);
        } catch (DataIntegrityViolationException raced) {
            // Another tab of the same browser inserted today's row first
            bump(visitorId, today, user);
        }
    }

    private boolean bump(String visitorId, LocalDate day, String user) {
        return repository.findByVisitorIdAndVisitDate(visitorId, day).map(v -> {
            v.setVisits(v.getVisits() + 1);
            v.setLastSeenAt(Instant.now());
            if (user != null) v.setUserId(user);
            repository.save(v);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    public Stats stats(int days) {
        int span = Math.max(7, Math.min(days, 365));
        LocalDate today = LocalDate.now(IST);

        Map<LocalDate, Day> byDate = new HashMap<>();
        for (Object[] row : repository.dailySince(today.minusDays(span - 1L))) {
            LocalDate d = (LocalDate) row[0];
            byDate.put(d, new Day(d, ((Number) row[1]).longValue(), ((Number) row[2]).longValue()));
        }
        List<Day> daily = new ArrayList<>(span);
        for (int i = span - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            daily.add(byDate.getOrDefault(d, new Day(d, 0, 0)));
        }

        LocalDate yesterday = today.minusDays(1);
        return new Stats(
                totals(today, today),
                totals(yesterday, yesterday),
                totals(today.minusDays(6), today),
                totals(today.minusDays(29), today),
                totals(BEGINNING, today),
                daily);
    }

    /** Visitors are distinct browsers across the whole range, not a sum of daily counts. */
    private Totals totals(LocalDate from, LocalDate to) {
        List<Object[]> rows = repository.totalsBetween(from, to);
        if (rows.isEmpty()) return new Totals(0, 0, 0);
        Object[] r = rows.get(0);
        return new Totals(num(r[0]), num(r[1]), num(r[2]));
    }

    private static long num(Object o) {
        return o == null ? 0 : ((Number) o).longValue();
    }
}
