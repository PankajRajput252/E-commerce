package com.gunwala.consultation;

import com.gunwala.consultation.ConsultationDtos.*;
import com.gunwala.model.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Consultations for users.
 *
 *   GET  /api/consultations/topics                 (public)  bookable topics
 *   GET  /api/consultations/slots?days=30          (public)  free slots
 *   POST /api/consultations/bookings               (login)   book a slot
 *   GET  /api/consultations/bookings/mine          (login)   my bookings
 *   POST /api/consultations/bookings/{id}/cancel   (login)   cancel my booking
 *   GET  /api/consultations/google/callback        (public)  Google OAuth redirect (admin connect flow)
 */
@Slf4j
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService service;
    private final GoogleCalendarService google;
    private final String frontendUrl;

    public ConsultationController(ConsultationService service, GoogleCalendarService google,
                                  @Value("${consultation.frontend-url:https://bandookwale.com}") String frontendUrl) {
        this.service = service;
        this.google = google;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @GetMapping("/topics")
    public List<TopicResponse> topics() {
        return service.activeTopics();
    }

    @GetMapping("/slots")
    public List<SlotResponse> slots(@RequestParam(defaultValue = "30") int days) {
        return service.availableSlots(days);
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookRequest request, @AuthenticationPrincipal User user) {
        return service.book(user, request);
    }

    @GetMapping("/bookings/mine")
    public List<BookingResponse> mine(@AuthenticationPrincipal User user) {
        return service.myBookings(user);
    }

    @PostMapping("/bookings/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return service.cancelByUser(user, id);
    }

    /** Google sends the admin back here after they allow calendar access; then we return them to the admin page. */
    @GetMapping("/google/callback")
    public ResponseEntity<Void> googleCallback(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String state,
                                               @RequestParam(required = false) String error) {
        String result;
        if (error != null || code == null) {
            result = "google=error&message=" + enc(error == null ? "Google sign-in was cancelled" : "Google sign-in was cancelled (" + error + ")");
        } else {
            try {
                google.completeAuthorization(code, state);
                result = "google=connected";
            } catch (Exception e) {
                log.warn("Connecting Google for consultations failed: {}", e.getMessage());
                result = "google=error&message=" + enc(e.getMessage());
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(frontendUrl + "/admin/consultations?" + result));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
