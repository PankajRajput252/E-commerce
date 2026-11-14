package com.gunwala.consultation;

import com.gunwala.consultation.ConsultationDtos.*;
import com.gunwala.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Admin: consultation topics, slots, bookings, and the Google account that hosts the calls. */
@RestController
@RequestMapping("/api/admin/consultations")
@PreAuthorize("hasAuthority('ADMIN_USER')")
@RequiredArgsConstructor
public class AdminConsultationController {

    private final ConsultationService service;
    private final GoogleCalendarService google;

    // ─── Topics ─────────────────────────────────────────────────────────────

    @GetMapping("/topics")
    public List<TopicResponse> topics() {
        return service.allTopics();
    }

    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponse createTopic(@Valid @RequestBody TopicRequest request) {
        return service.createTopic(request);
    }

    @PutMapping("/topics/{id}")
    public TopicResponse updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequest request) {
        return service.updateTopic(id, request);
    }

    @DeleteMapping("/topics/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopic(@PathVariable Long id) {
        service.deleteTopic(id);
    }

    // ─── Slots ──────────────────────────────────────────────────────────────

    @GetMapping("/slots")
    public List<AdminSlotResponse> slots(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        checkRange(from, to);
        return service.slotsBetween(from, to);
    }

    @PostMapping("/slots/generate")
    public GenerateSlotsResponse generate(@Valid @RequestBody GenerateSlotsRequest request) {
        return service.generateSlots(request);
    }

    @DeleteMapping("/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable Long id) {
        service.deleteSlot(id);
    }

    // ─── Bookings ───────────────────────────────────────────────────────────

    @GetMapping("/bookings")
    public List<BookingResponse> bookings(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        checkRange(from, to);
        return service.bookingsBetween(from, to);
    }

    @PostMapping("/bookings/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, @Valid @RequestBody(required = false) CancelRequest request) {
        return service.cancelByAdmin(id, request == null ? null : request.reason());
    }

    @PutMapping("/bookings/{id}/meet-link")
    public BookingResponse setMeetLink(@PathVariable Long id, @Valid @RequestBody MeetLinkRequest request) {
        return service.setMeetLink(id, request.meetLink());
    }

    @PostMapping("/bookings/{id}/meet-link/retry")
    public BookingResponse retryMeetLink(@PathVariable Long id) {
        return service.retryMeetLink(id);
    }

    // ─── Google account ─────────────────────────────────────────────────────

    @GetMapping("/google")
    public GoogleStatusResponse googleStatus() {
        var account = google.isConfigured() ? google.account() : java.util.Optional.<GoogleCalendarAccount>empty();
        return new GoogleStatusResponse(google.isConfigured(), account.isPresent(),
                account.map(GoogleCalendarAccount::getAccountEmail).orElse(null),
                account.map(GoogleCalendarAccount::getConnectedAt).orElse(null));
    }

    @GetMapping("/google/authorize-url")
    public AuthorizeUrlResponse authorizeUrl() {
        if (!google.isConfigured()) {
            throw new BadRequestException("Google isn't set up on the server yet (GOOGLE_OAUTH_CLIENT_ID / SECRET / REDIRECT_URI).");
        }
        return new AuthorizeUrlResponse(google.authorizeUrl());
    }

    @DeleteMapping("/google")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnectGoogle() {
        google.disconnect();
    }

    private static void checkRange(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) throw new BadRequestException("'to' must be on or after 'from'");
        if (from.plusDays(366).isBefore(to)) throw new BadRequestException("Choose a range of at most a year");
    }
}
