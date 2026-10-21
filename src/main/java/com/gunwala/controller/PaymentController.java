package com.gunwala.controller;

import com.gunwala.dto.PaymentSubmissionRequest;
import com.gunwala.dto.PaymentSubmissionResponse;
import com.gunwala.service.Service.PaymentSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/** End user: submit a payment with receipt and track its status. */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentSubmissionService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentSubmissionResponse submit(@Valid @ModelAttribute PaymentSubmissionRequest request,
                                            @RequestPart("receipt") MultipartFile receipt,
                                            Principal user) {
        return service.submit(user.getName(), request, receipt);
    }

    @GetMapping
    public List<PaymentSubmissionResponse> myPayments(Principal user) {
        return service.listForUser(user.getName());
    }

    @GetMapping("/{id}")
    public PaymentSubmissionResponse get(@PathVariable Long id, Principal user) {
        return service.getForUser(id, user.getName());
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> receipt(@PathVariable Long id, Principal user) {
        return service.loadReceiptForUser(id, user.getName()).toResponseEntity();
    }
}
