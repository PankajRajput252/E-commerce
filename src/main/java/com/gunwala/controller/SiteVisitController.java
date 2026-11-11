package com.gunwala.controller;

import com.gunwala.dto.RecordVisitRequest;
import com.gunwala.service.Service.SiteVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SiteVisitController {

    private final SiteVisitService service;

    /** Public (guests too): the frontend calls this once per browser session. */
    @PostMapping("/api/site-visits")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void record(@Valid @RequestBody RecordVisitRequest request) {
        service.record(request.visitorId(), request.userId());
    }

    /** Admin dashboard: today / yesterday / 7 / 30 days / all time, plus a daily series. */
    @GetMapping("/api/admin/site-visits/stats")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public SiteVisitService.Stats stats(@RequestParam(defaultValue = "30") int days) {
        return service.stats(days);
    }
}
