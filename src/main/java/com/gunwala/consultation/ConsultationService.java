package com.gunwala.consultation;

import com.gunwala.consultation.ConsultationDtos.*;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consultation booking. Bookings are saved in a short transaction (with a row lock on the slot so it
 * can't be double-booked); the Google Meet link and emails are handled after it commits, so a slow or
 * failing Google / mail call never loses a booking.
 */
@Slf4j
@Service
public class ConsultationService {

    /** A user can hold at most this many upcoming consultations at once. */
    static final int MAX_UPCOMING_PER_USER = 3;
    private static final int MAX_DAYS_AHEAD = 90;
    private static final int MAX_SLOTS_PER_GENERATE = 1000;

    private final ConsultationTopicRepository topics;
    private final ConsultationSlotRepository slots;
    private final ConsultationBookingRepository bookings;
    private final GoogleCalendarService google;
    private final ConsultationMailService mail;
    private final TransactionTemplate tx;
    private final ZoneId zone;
    private final Duration minLead;

    public ConsultationService(ConsultationTopicRepository topics,
                               ConsultationSlotRepository slots,
                               ConsultationBookingRepository bookings,
                               GoogleCalendarService google,
                               ConsultationMailService mail,
                               PlatformTransactionManager txManager,
                               @Value("${consultation.timezone:Asia/Kolkata}") String timezone,
                               @Value("${consultation.min-lead-minutes:60}") long minLeadMinutes) {
        this.topics = topics;
        this.slots = slots;
        this.bookings = bookings;
        this.google = google;
        this.mail = mail;
        this.tx = new TransactionTemplate(txManager);
        this.zone = ZoneId.of(timezone);
        this.minLead = Duration.ofMinutes(minLeadMinutes);
    }

    // ============================================================ public

    public List<TopicResponse> activeTopics() {
        return topics.findByActiveTrueOrderBySortOrderAscNameAsc().stream().map(TopicResponse::of).toList();
    }

    /** Free slots users can still book: from now + lead time, up to {@code days} ahead. */
    public List<SlotResponse> availableSlots(int days) {
        Instant from = Instant.now().plus(minLead);
        Instant to = Instant.now().plus(Duration.ofDays(Math.max(1, Math.min(days, MAX_DAYS_AHEAD))));
        return slots.findByBookedFalseAndStartsAtBetweenOrderByStartsAtAsc(from, to).stream().map(SlotResponse::of).toList();
    }

    // ============================================================ user

    public BookingResponse book(User user, BookRequest req) {
        String userId = nodeIdOf(user);
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BadRequestException("Add an email address to your profile so we can send you the meeting link.");
        }

        ConsultationBooking booking = tx.execute(status -> {
            ConsultationTopic topic = topics.findById(req.topicId())
                    .filter(ConsultationTopic::isActive)
                    .orElseThrow(() -> new BadRequestException("This topic is no longer available. Please pick another."));

            Instant now = Instant.now();
            if (bookings.findByUserIdAndStatusAndStartsAtAfter(userId, ConsultationBooking.Status.CONFIRMED, now).size() >= MAX_UPCOMING_PER_USER) {
                throw new ConflictException("You already have " + MAX_UPCOMING_PER_USER
                        + " upcoming consultations. Please attend or cancel one before booking another.");
            }

            ConsultationSlot slot = slots.findByIdForUpdate(req.slotId())
                    .orElseThrow(() -> new NotFoundException("This time is no longer available. Please pick another."));
            if (slot.isBooked()) {
                throw new ConflictException("Sorry, someone just booked this time. Please pick another slot.");
            }
            if (slot.getStartsAt().isBefore(now.plus(minLead))) {
                throw new BadRequestException("This time is too soon to book. Please pick a later slot.");
            }

            slot.setBooked(true);
            ConsultationBooking b = new ConsultationBooking();
            b.setSlot(slot);
            b.setStartsAt(slot.getStartsAt());
            b.setDurationMinutes(slot.getDurationMinutes());
            b.setTopicId(topic.getId());
            b.setTopicName(topic.getName());
            b.setUserId(userId);
            b.setUserName(trimToNull(user.getName()));
            b.setUserEmail(user.getEmail().trim());
            b.setPhone(trimToNull(req.phone()));
            b.setNotes(trimToNull(req.notes()));
            b.setStatus(ConsultationBooking.Status.CONFIRMED);
            return bookings.save(b);
        });

        attachMeeting(booking);
        mail.bookingConfirmed(booking);
        return BookingResponse.of(booking);
    }

    public List<BookingResponse> myBookings(User user) {
        return bookings.findByUserIdOrderByCreatedAtDesc(nodeIdOf(user)).stream().map(BookingResponse::of).toList();
    }

    public BookingResponse cancelByUser(User user, Long bookingId) {
        String userId = nodeIdOf(user);
        return cancel(bookingId, b -> {
            if (!b.getUserId().equals(userId)) throw new NotFoundException("Booking not found");
            if (!b.getStartsAt().isAfter(Instant.now())) {
                throw new BadRequestException("This consultation has already started and can't be cancelled.");
            }
            b.setCancelledBy(ConsultationBooking.CancelledBy.USER);
        });
    }

    // ============================================================ admin: topics

    public List<TopicResponse> allTopics() {
        return topics.findAllByOrderBySortOrderAscNameAsc().stream().map(TopicResponse::of).toList();
    }

    public TopicResponse createTopic(TopicRequest req) {
        ConsultationTopic t = new ConsultationTopic();
        apply(t, req);
        return TopicResponse.of(topics.save(t));
    }

    public TopicResponse updateTopic(Long id, TopicRequest req) {
        ConsultationTopic t = topics.findById(id).orElseThrow(() -> new NotFoundException("Topic not found"));
        apply(t, req);
        return TopicResponse.of(topics.save(t));
    }

    /** Past bookings keep the topic's name, so deleting is safe. */
    public void deleteTopic(Long id) {
        if (!topics.existsById(id)) throw new NotFoundException("Topic not found");
        topics.deleteById(id);
    }

    private void apply(ConsultationTopic t, TopicRequest req) {
        t.setName(req.name().trim());
        t.setDescription(trimToNull(req.description()));
        if (req.active() != null) t.setActive(req.active());
        if (req.sortOrder() != null) t.setSortOrder(req.sortOrder());
    }

    // ============================================================ admin: slots

    /** Every slot between the two dates (consultation time zone), with who booked it. */
    public List<AdminSlotResponse> slotsBetween(LocalDate from, LocalDate to) {
        Instant start = from.atStartOfDay(zone).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(zone).toInstant();
        List<ConsultationSlot> list = slots.findByStartsAtBetweenOrderByStartsAtAsc(start, end);
        Map<Long, ConsultationBooking> bySlot = list.isEmpty() ? Map.of()
                : bookings.findBySlotIdInAndStatus(list.stream().map(ConsultationSlot::getId).toList(), ConsultationBooking.Status.CONFIRMED).stream()
                .collect(Collectors.toMap(b -> b.getSlot().getId(), Function.identity(), (a, b) -> a));
        return list.stream().map(s -> {
            ConsultationBooking b = bySlot.get(s.getId());
            return new AdminSlotResponse(s.getId(), s.getStartsAt(), s.getEndsAt(), s.getDurationMinutes(), s.isBooked(),
                    b == null ? null : b.getId(), b == null ? null : b.getUserName(), b == null ? null : b.getTopicName());
        }).toList();
    }

    public GenerateSlotsResponse generateSlots(GenerateSlotsRequest req) {
        if (req.toDate().isBefore(req.fromDate())) throw new BadRequestException("'To' date must be on or after the 'From' date.");
        if (req.fromDate().isBefore(LocalDate.now(zone))) throw new BadRequestException("'From' date can't be in the past.");
        if (req.toDate().isAfter(LocalDate.now(zone).plusDays(MAX_DAYS_AHEAD))) {
            throw new BadRequestException("Slots can be created up to " + MAX_DAYS_AHEAD + " days ahead.");
        }
        int duration = req.durationMinutes();
        int gap = req.breakMinutes() == null ? 0 : req.breakMinutes();
        if (!req.dayEnd().isAfter(req.dayStart().plusMinutes(duration - 1L)) || req.dayEnd().isBefore(req.dayStart())) {
            throw new BadRequestException("The day must be long enough for at least one " + duration + "-minute slot.");
        }
        Set<Integer> weekdays = req.weekdays() == null || req.weekdays().isEmpty() ? Set.of(1, 2, 3, 4, 5, 6, 7) : req.weekdays();

        Instant rangeStart = req.fromDate().atStartOfDay(zone).toInstant();
        Instant rangeEnd = req.toDate().plusDays(1).atStartOfDay(zone).toInstant();
        List<ConsultationSlot> existing = new ArrayList<>(slots.findByStartsAtBetweenOrderByStartsAtAsc(
                rangeStart.minus(Duration.ofHours(5)), rangeEnd));
        Instant earliest = Instant.now().plus(minLead);

        List<ConsultationSlot> toCreate = new ArrayList<>();
        int skipped = 0;
        for (LocalDate d = req.fromDate(); !d.isAfter(req.toDate()); d = d.plusDays(1)) {
            if (!weekdays.contains(d.getDayOfWeek().getValue())) continue;
            for (LocalTime t = req.dayStart(); !t.plusMinutes(duration).isAfter(req.dayEnd()); ) {
                Instant start = ZonedDateTime.of(d, t, zone).toInstant();
                Instant end = start.plusSeconds(duration * 60L);
                if (start.isBefore(earliest) || overlaps(existing, start, end)) {
                    skipped++;
                } else {
                    ConsultationSlot s = new ConsultationSlot();
                    s.setStartsAt(start);
                    s.setDurationMinutes(duration);
                    toCreate.add(s);
                    existing.add(s);
                }
                LocalTime next = t.plusMinutes(duration + (long) gap);
                if (!next.isAfter(t)) break; // wrapped past midnight
                t = next;
            }
            if (toCreate.size() > MAX_SLOTS_PER_GENERATE) {
                throw new BadRequestException("That would create more than " + MAX_SLOTS_PER_GENERATE + " slots. Use a shorter date range.");
            }
        }
        slots.saveAll(toCreate);
        return new GenerateSlotsResponse(toCreate.size(), skipped);
    }

    private static boolean overlaps(List<ConsultationSlot> existing, Instant start, Instant end) {
        for (ConsultationSlot s : existing) {
            if (s.getStartsAt().isBefore(end) && start.isBefore(s.getEndsAt())) return true;
        }
        return false;
    }

    public void deleteSlot(Long id) {
        tx.executeWithoutResult(status -> {
            ConsultationSlot slot = slots.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Slot not found"));
            if (slot.isBooked()) {
                throw new ConflictException("This slot is booked. Cancel the booking first (the user will be emailed).");
            }
            // Cancelled bookings keep their own copy of the time
            for (ConsultationBooking b : bookings.findBySlotId(id)) {
                b.setSlot(null);
            }
            slots.delete(slot);
        });
    }

    // ============================================================ admin: bookings

    public List<BookingResponse> bookingsBetween(LocalDate from, LocalDate to) {
        Instant start = from.atStartOfDay(zone).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(zone).toInstant();
        return bookings.findByStartsAtBetweenOrderByStartsAtAsc(start, end).stream().map(BookingResponse::of).toList();
    }

    public BookingResponse cancelByAdmin(Long bookingId, String reason) {
        return cancel(bookingId, b -> {
            b.setCancelledBy(ConsultationBooking.CancelledBy.ADMIN);
            b.setCancelReason(trimToNull(reason));
        });
    }

    /** Admin pastes a link (e.g. when Google isn't connected); the user is emailed it. */
    public BookingResponse setMeetLink(Long bookingId, String link) {
        ConsultationBooking b = tx.execute(status -> {
            ConsultationBooking booking = confirmedUpcoming(bookingId);
            booking.setMeetLink(link.trim());
            return bookings.save(booking);
        });
        mail.meetLinkReady(b);
        return BookingResponse.of(b);
    }

    /** Try creating the Google Meet link again (e.g. after connecting Google). */
    public BookingResponse retryMeetLink(Long bookingId) {
        if (!google.isConnected()) throw new BadRequestException("Connect a Google account first (Google Meet section above).");
        ConsultationBooking b = confirmedUpcoming(bookingId);
        if (StringUtils.hasText(b.getCalendarEventId())) {
            try {
                google.cancelMeeting(b.getCalendarEventId()); // replace any half-created event
            } catch (Exception e) {
                log.warn("Could not remove old calendar event for booking {}: {}", bookingId, e.getMessage());
            }
            b.setCalendarEventId(null);
        }
        try {
            GoogleCalendarService.CreatedEvent event = google.createMeeting(b);
            b.setCalendarEventId(event.eventId());
            b.setMeetLink(event.meetLink());
            b = bookings.save(b);
        } catch (Exception e) {
            throw new BadRequestException("Google couldn't create the meeting: " + e.getMessage());
        }
        mail.meetLinkReady(b);
        return BookingResponse.of(b);
    }

    // ============================================================ shared

    private ConsultationBooking confirmedUpcoming(Long bookingId) {
        ConsultationBooking b = bookings.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (b.getStatus() != ConsultationBooking.Status.CONFIRMED) throw new BadRequestException("This booking was cancelled.");
        if (!b.getEndsAt().isAfter(Instant.now())) throw new BadRequestException("This consultation is already over.");
        return b;
    }

    private BookingResponse cancel(Long bookingId, java.util.function.Consumer<ConsultationBooking> check) {
        ConsultationBooking b = tx.execute(status -> {
            ConsultationBooking booking = bookings.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
            if (booking.getStatus() == ConsultationBooking.Status.CANCELLED) {
                throw new BadRequestException("This booking is already cancelled.");
            }
            check.accept(booking);
            booking.setStatus(ConsultationBooking.Status.CANCELLED);
            booking.setCancelledAt(Instant.now());
            if (booking.getSlot() != null) {
                slots.findByIdForUpdate(booking.getSlot().getId()).ifPresent(s -> s.setBooked(false));
            }
            return bookings.save(booking);
        });

        if (StringUtils.hasText(b.getCalendarEventId()) && google.isConnected()) {
            try {
                google.cancelMeeting(b.getCalendarEventId());
            } catch (Exception e) {
                log.warn("Could not cancel calendar event for booking {}: {}", b.getId(), e.getMessage());
            }
        }
        mail.bookingCancelled(b);
        return BookingResponse.of(b);
    }

    /** Creates the Calendar event + Meet link. A failure is logged; the booking stays confirmed without a link. */
    private void attachMeeting(ConsultationBooking b) {
        if (!google.isConnected()) {
            log.warn("Booking {} has no Meet link: no Google account connected for consultations", b.getId());
            return;
        }
        try {
            GoogleCalendarService.CreatedEvent event = google.createMeeting(b);
            b.setCalendarEventId(event.eventId());
            b.setMeetLink(event.meetLink());
            bookings.save(b);
        } catch (Exception e) {
            log.error("Could not create Google Meet for booking {}: {}", b.getId(), e.getMessage());
        }
    }

    private static String nodeIdOf(User user) {
        if (user == null || !StringUtils.hasText(user.getNodeId())) {
            throw new BadRequestException("Please sign in again to book a consultation.");
        }
        return user.getNodeId();
    }

    private static String trimToNull(String s) {
        return StringUtils.hasText(s) ? s.trim() : null;
    }
}
