package com.gunwala.controller;

import com.gunwala.dto.PageResponse;
import com.gunwala.dto.PaymentSubmissionResponse;
import com.gunwala.dto.RejectPaymentRequest;
import com.gunwala.entity.PaymentStatus;
import com.gunwala.service.Service.PaymentSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/** Admin reviews submitted payments and approves or rejects them. */
@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentReviewController {

    private final PaymentSubmissionService service;

    /** e.g. GET /api/admin/payments?status=PENDING&page=0&size=20 */
    @GetMapping
    public PageResponse<PaymentSubmissionResponse> list(
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listForAdmin(status, pageable);
    }

    @GetMapping("/{id}")
    public PaymentSubmissionResponse get(@PathVariable Long id) {
        return service.getForAdmin(id);
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> receipt(@PathVariable Long id) {
        return service.loadReceiptForAdmin(id).toResponseEntity();
    }

    @PutMapping("/{id}/approve")
    public PaymentSubmissionResponse approve(@PathVariable Long id, Principal admin) {
        return service.approve(id, admin.getName());
    }

    @PutMapping("/{id}/reject")
    public PaymentSubmissionResponse reject(@PathVariable Long id,
                                            @Valid @RequestBody RejectPaymentRequest request,
                                            Principal admin) {
        return service.reject(id, admin.getName(), request.reason());
    }
}
