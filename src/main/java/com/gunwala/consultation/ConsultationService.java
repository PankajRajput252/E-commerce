package com.gunwala.consultation;

import com.gunwala.consultation.ConsultationDtos.*;
import com.gunwala.entity.PaymentStatus;
import com.gunwala.entity.PaymentSubmission;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.model.User;
import com.gunwala.repository.PaymentSubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consultation booking. Bookings are saved in a short transaction (with a row lock on the slot so it
 * can't be double-booked); the Google Meet link and emails are handled after it commits, so a slow or
 * failing Google / mail call never loses a booking.
 *
 * Paid topics: booking holds the slot as PENDING_PAYMENT. The user pays by QR / bank transfer and submits the
 * receipt (PaymentSubmissionService, linked by consultationBookingId). Admin approval confirms the booking and
 * only then creates the Meet link and sends the confirmation email. A hold with no payment submitted is
 * released after {@code consultation.payment-hold-minutes}.
 */
@Slf4j
@Service
public class ConsultationService {

    /** A user can hold at most this many upcoming consultations at once. */
    static final int MAX_UPCOMING_PER_USER = 3;
    /** A booking can be moved to another slot this many times. */
    static final int MAX_RESCHEDULES = 1;
    private static final int MAX_DAYS_AHEAD = 90;
    private static final int MAX_SLOTS_PER_GENERATE = 1000;
    /** Bookings that hold a slot. */
    private static final List<ConsultationBooking.Status> ACTIVE =
            List.of(ConsultationBooking.Status.CONFIRMED, ConsultationBooking.Status.PENDING_PAYMENT);
    /** Payments that block another payment / expiry for the same booking. */
    private static final List<PaymentStatus> LIVE_PAYMENT = List.of(PaymentStatus.PENDING, PaymentStatus.APPROVED);

    private final ConsultationTopicRepository topics;
    private final ConsultationSlotRepository slots;
    private final ConsultationBookingRepository bookings;
    private final GoogleCalendarService google;
    private final ConsultationMailService mail;
    private final PaymentSubmissionRepository payments;
    private final TransactionTemplate tx;
    /** For DB writes from afterCommit callbacks, where the finished transaction is still bound. */
    private final TransactionTemplate txNew;
    private final ZoneId zone;
    private final Duration minLead;
    private final Duration paymentHold;

    public ConsultationService(ConsultationTopicRepository topics,
                               ConsultationSlotRepository slots,
                               ConsultationBookingRepository bookings,
                               GoogleCalendarService google,
                               ConsultationMailService mail,
                               PaymentSubmissionRepository payments,
                               PlatformTransactionManager txManager,
                               @Value("${consultation.timezone:Asia/Kolkata}") String timezone,
                               @Value("${consultation.min-lead-minutes:60}") long minLeadMinutes,
                               @Value("${consultation.payment-hold-minutes:30}") long paymentHoldMinutes) {
        this.topics = topics;
        this.slots = slots;
        this.bookings = bookings;
        this.google = google;
        this.mail = mail;
        this.payments = payments;
        this.tx = new TransactionTemplate(txManager);
        this.txNew = new TransactionTemplate(txManager);
        this.txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.zone = ZoneId.of(timezone);
        this.minLead = Duration.ofMinutes(minLeadMinutes);
        this.paymentHold = Duration.ofMinutes(Math.max(5, paymentHoldMinutes));
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
            if (bookings.findByUserIdAndStatusInAndStartsAtAfter(userId, ACTIVE, now).size() >= MAX_UPCOMING_PER_USER) {
                throw new ConflictException("You already have " + MAX_UPCOMING_PER_USER
                        + " upcoming consultations (including any awaiting payment). Please attend or cancel one before booking another.");
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
            if (topic.isPaid()) {
                // Hold the slot until the user pays; confirmed when an admin approves the payment
                b.setAmount(topic.getPrice());
                b.setStatus(ConsultationBooking.Status.PENDING_PAYMENT);
                b.setPaymentDueAt(holdUntil(now, slot.getStartsAt()));
            } else {
                b.setStatus(ConsultationBooking.Status.CONFIRMED);
            }
            return bookings.save(b);
        });

        if (booking.getStatus() == ConsultationBooking.Status.CONFIRMED) {
            attachMeeting(booking);
            mail.bookingConfirmed(booking);
        }
        return BookingResponse.of(booking);
    }

    public List<BookingResponse> myBookings(User user) {
        return withPayments(bookings.findByUserIdOrderByCreatedAtDesc(nodeIdOf(user)));
    }

    public BookingResponse cancelByUser(User user, Long bookingId) {
        String userId = nodeIdOf(user);
        return cancel(bookingId, b -> {
            if (!b.getUserId().equals(userId)) throw new NotFoundException("Booking not found");
            if (!b.getStartsAt().isAfter(Instant.now())) {
                throw new BadRequestException("This consultation has already started and can't be cancelled.");
            }
            if (b.getStatus() == ConsultationBooking.Status.PENDING_PAYMENT
                    && payments.existsByConsultationBookingIdAndStatusIn(b.getId(), List.of(PaymentStatus.PENDING))) {
                throw new ConflictException("Your payment for this consultation is being verified, so it can't be cancelled right now. "
                        + "Please contact support.");
            }
            b.setCancelledBy(ConsultationBooking.CancelledBy.USER);
        });
    }

    /**
     * Moves the user's booking to another free slot. The payment (if any) stays with the booking, so a paid
     * consultation can be rescheduled instead of cancelled (fees aren't refunded on cancellation).
     */
    public BookingResponse rescheduleByUser(User user, Long bookingId, Long newSlotId) {
        String userId = nodeIdOf(user);
        Instant[] oldStart = new Instant[1];
        String[] oldEventId = new String[1];

        ConsultationBooking booking = tx.execute(status -> {
            ConsultationBooking b = bookings.findByIdForUpdate(bookingId)
                    .filter(x -> x.getUserId().equals(userId))
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
            if (b.getStatus() == ConsultationBooking.Status.CANCELLED) {
                throw new BadRequestException("This booking is cancelled and can't be rescheduled.");
            }
            if (b.getRescheduleCount() >= MAX_RESCHEDULES) {
                throw new BadRequestException("This booking has already been rescheduled once and can't be moved again.");
            }
            Instant now = Instant.now();
            if (!b.getStartsAt().isAfter(now)) {
                throw new BadRequestException("This consultation has already started and can't be rescheduled.");
            }
            if (b.getSlot() != null && b.getSlot().getId().equals(newSlotId)) {
                throw new BadRequestException("Your consultation is already at this time. Please pick a different slot.");
            }

            ConsultationSlot slot = slots.findByIdForUpdate(newSlotId)
                    .orElseThrow(() -> new NotFoundException("This time is no longer available. Please pick another."));
            if (slot.isBooked()) {
                throw new ConflictException("Sorry, someone just booked this time. Please pick another slot.");
            }
            if (slot.getStartsAt().isBefore(now.plus(minLead))) {
                throw new BadRequestException("This time is too soon to book. Please pick a later slot.");
            }

            if (b.getSlot() != null) {
                slots.findByIdForUpdate(b.getSlot().getId()).ifPresent(s -> s.setBooked(false));
            }
            slot.setBooked(true);
            oldStart[0] = b.getStartsAt();
            b.setRescheduleCount(b.getRescheduleCount() + 1);
            b.setSlot(slot);
            b.setStartsAt(slot.getStartsAt());
            b.setDurationMinutes(slot.getDurationMinutes());
            if (b.getPaymentDueAt() != null && b.getPaymentDueAt().isAfter(slot.getStartsAt())) {
                b.setPaymentDueAt(slot.getStartsAt());
            }
            // A Google-created meeting is for the old time: replace it. A link an admin pasted is kept.
            if (StringUtils.hasText(b.getCalendarEventId())) {
                oldEventId[0] = b.getCalendarEventId();
                b.setCalendarEventId(null);
                b.setMeetLink(null);
            }
            return bookings.save(b);
        });

        if (oldEventId[0] != null && google.isConnected()) {
            try {
                google.cancelMeeting(oldEventId[0]);
            } catch (Exception e) {
                log.warn("Could not cancel old calendar event for rescheduled booking {}: {}", bookingId, e.getMessage());
            }
        }
        if (booking.getStatus() == ConsultationBooking.Status.CONFIRMED) {
            if (!StringUtils.hasText(booking.getMeetLink())) attachMeeting(booking);
            mail.bookingRescheduled(booking, oldStart[0]);
        }
        return withPayments(List.of(booking)).get(0);
    }

    // ============================================================ payment (called by PaymentSubmissionService)

    /**
     * Checks a payment the user is submitting for a consultation, inside the payment's transaction.
     * Locks the booking so the hold can't expire while the payment is being saved, and stops the hold timer.
     */
    public void attachPayment(Long bookingId, String userId, BigDecimal amount) {
        ConsultationBooking b = bookings.findByIdForUpdate(bookingId)
                .filter(x -> x.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Consultation booking " + bookingId + " not found"));
        switch (b.getStatus()) {
            case CONFIRMED -> throw new ConflictException("This consultation is already paid and confirmed.");
            case CANCELLED -> throw new ConflictException("This booking was cancelled"
                    + (b.getCancelledBy() == null ? " because payment wasn't received in time" : "")
                    + ". Please book the consultation again.");
            case PENDING_PAYMENT -> { }
        }
        if (!b.getStartsAt().isAfter(Instant.now())) {
            throw new BadRequestException("This consultation's time has passed. Please book another slot.");
        }
        if (b.getAmount() == null || amount == null || b.getAmount().compareTo(amount) != 0) {
            throw new BadRequestException("The amount for this consultation is ₹"
                    + (b.getAmount() == null ? "0" : b.getAmount().toPlainString()) + ".");
        }
        if (payments.existsByConsultationBookingIdAndStatusIn(bookingId, LIVE_PAYMENT)) {
            throw new ConflictException("A payment for this consultation has already been submitted and is waiting for verification.");
        }
        b.setPaymentDueAt(null); // payment under review: keep holding the slot
    }

    /**
     * Admin approved the payment: confirm the booking (in the approval's transaction), then create the
     * Meet link and email the user once it commits.
     */
    public void markPaid(Long bookingId) {
        ConsultationBooking b = bookings.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new NotFoundException("Consultation booking " + bookingId + " not found"));
        if (b.getStatus() != ConsultationBooking.Status.PENDING_PAYMENT) {
            throw new ConflictException("Consultation booking #" + bookingId + " is " + b.getStatus().name().toLowerCase()
                    + ", so it can't be confirmed. Reject this payment and refund the user.");
        }
        b.setStatus(ConsultationBooking.Status.CONFIRMED);
        b.setPaymentDueAt(null);
        afterCommit(() -> {
            attachMeeting(b);
            mail.bookingConfirmed(b);
        });
    }

    /** Admin rejected the payment: the user gets a fresh hold window to pay again. */
    public void paymentRejected(Long bookingId) {
        bookings.findByIdForUpdate(bookingId)
                .filter(b -> b.getStatus() == ConsultationBooking.Status.PENDING_PAYMENT)
                .ifPresent(b -> b.setPaymentDueAt(holdUntil(Instant.now(), b.getStartsAt())));
    }

    /** Releases slots held for payments that never arrived. */
    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT1M")
    public void releaseUnpaidHolds() {
        List<ConsultationBooking> due = bookings.findByStatusAndPaymentDueAtBefore(ConsultationBooking.Status.PENDING_PAYMENT, Instant.now());
        for (ConsultationBooking candidate : due) {
            try {
                tx.executeWithoutResult(status -> {
                    ConsultationBooking b = bookings.findByIdForUpdate(candidate.getId()).orElse(null);
                    if (b == null || b.getStatus() != ConsultationBooking.Status.PENDING_PAYMENT
                            || b.getPaymentDueAt() == null || b.getPaymentDueAt().isAfter(Instant.now())
                            || payments.existsByConsultationBookingIdAndStatusIn(b.getId(), LIVE_PAYMENT)) {
                        return;
                    }
                    b.setStatus(ConsultationBooking.Status.CANCELLED);
                    b.setCancelledAt(Instant.now());
                    b.setCancelReason("Payment wasn't received in time");
                    b.setPaymentDueAt(null);
                    if (b.getSlot() != null) {
                        slots.findByIdForUpdate(b.getSlot().getId()).ifPresent(s -> s.setBooked(false));
                    }
                    log.info("Released consultation booking {}: no payment received", b.getId());
                });
            } catch (Exception e) {
                log.warn("Could not release unpaid consultation booking {}: {}", candidate.getId(), e.getMessage());
            }
        }
    }

    /** Payment deadline: the hold window, but never later than the slot's start. */
    private Instant holdUntil(Instant now, Instant startsAt) {
        Instant due = now.plus(paymentHold);
        return due.isAfter(startsAt) ? startsAt : due;
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
        // null or 0 = free. Existing bookings keep the amount they were booked at.
        t.setPrice(req.price() == null || req.price().signum() <= 0 ? null : req.price());
    }

    // ============================================================ admin: slots

    /** Every slot between the two dates (consultation time zone), with who booked it. */
    public List<AdminSlotResponse> slotsBetween(LocalDate from, LocalDate to) {
        Instant start = from.atStartOfDay(zone).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(zone).toInstant();
        List<ConsultationSlot> list = slots.findByStartsAtBetweenOrderByStartsAtAsc(start, end);
        Map<Long, ConsultationBooking> bySlot = list.isEmpty() ? Map.of()
                : bookings.findBySlotIdInAndStatusIn(list.stream().map(ConsultationSlot::getId).toList(), ACTIVE).stream()
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
        return withPayments(bookings.findByStartsAtBetweenOrderByStartsAtAsc(start, end));
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
        if (b.getStatus() == ConsultationBooking.Status.PENDING_PAYMENT) {
            throw new BadRequestException("This booking is waiting for payment. Approve the payment first.");
        }
        if (b.getStatus() != ConsultationBooking.Status.CONFIRMED) throw new BadRequestException("This booking was cancelled.");
        if (!b.getEndsAt().isAfter(Instant.now())) throw new BadRequestException("This consultation is already over.");
        return b;
    }

    private BookingResponse cancel(Long bookingId, java.util.function.Consumer<ConsultationBooking> check) {
        boolean[] paidAndConfirmed = new boolean[1];
        ConsultationBooking b = tx.execute(status -> {
            ConsultationBooking booking = bookings.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
            if (booking.getStatus() == ConsultationBooking.Status.CANCELLED) {
                throw new BadRequestException("This booking is already cancelled.");
            }
            check.accept(booking);
            paidAndConfirmed[0] = booking.getStatus() == ConsultationBooking.Status.CONFIRMED && booking.getAmount() != null;
            booking.setStatus(ConsultationBooking.Status.CANCELLED);
            booking.setCancelledAt(Instant.now());
            booking.setPaymentDueAt(null);
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
        mail.bookingCancelled(b, paidAndConfirmed[0]);
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
            txNew.executeWithoutResult(status -> bookings.save(b));
        } catch (Exception e) {
            log.error("Could not create Google Meet for booking {}: {}", b.getId(), e.getMessage());
        }
    }

    /** Bookings with their latest payment (if any), for showing payment status. */
    private List<BookingResponse> withPayments(List<ConsultationBooking> list) {
        List<Long> paidIds = list.stream().filter(b -> b.getAmount() != null).map(ConsultationBooking::getId).toList();
        Map<Long, PaymentSubmission> latest = new HashMap<>();
        if (!paidIds.isEmpty()) {
            for (PaymentSubmission p : payments.findByConsultationBookingIdInOrderByCreatedAtDesc(paidIds)) {
                latest.putIfAbsent(p.getConsultationBookingId(), p);
            }
        }
        return list.stream().map(b -> BookingResponse.of(b, latest.get(b.getId()))).toList();
    }

    /** Runs after the current transaction commits, or right away if there is none. */
    private static void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
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
