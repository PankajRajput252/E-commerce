package com.gunwala.controller;


import com.gunwala.dto.BankAccountRequest;
import com.gunwala.dto.PaymentMethodResponse;
import com.gunwala.service.Service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Admin configures the bank account / QR code options users can pay with. */
@RestController
@RequestMapping("/api/admin/payment-methods")
@RequiredArgsConstructor
public class AdminPaymentMethodController {

    private final PaymentMethodService service;

    @GetMapping
    public List<PaymentMethodResponse> listAll() {
        return service.listAll();
    }

    @PostMapping("/bank-account")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodResponse createBankAccount(@Valid @RequestBody BankAccountRequest request) {
        return service.createBankAccount(request);
    }

    @PutMapping("/bank-account/{id}")
    public PaymentMethodResponse updateBankAccount(@PathVariable Long id, @Valid @RequestBody BankAccountRequest request) {
        return service.updateBankAccount(id, request);
    }

    @PostMapping(value = "/qr-code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodResponse createQrCode(@RequestParam String displayName,
                                              @RequestParam(required = false) String upiId,
                                              @RequestPart("qrImage") MultipartFile qrImage) {
        return service.createQrCode(displayName, upiId, qrImage);
    }

    @PutMapping(value = "/qr-code/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PaymentMethodResponse updateQrCode(@PathVariable Long id,
                                              @RequestParam(required = false) String displayName,
                                              @RequestParam(required = false) String upiId,
                                              @RequestPart(value = "qrImage", required = false) MultipartFile qrImage) {
        return service.updateQrCode(id, displayName, upiId, qrImage);
    }

    /** Enable / disable a method. Methods are never hard-deleted (past payments reference them). */
    @PatchMapping("/{id}/status")
    public PaymentMethodResponse setActive(@PathVariable Long id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }

    @GetMapping("/{id}/qr-image")
    public ResponseEntity<Resource> qrImage(@PathVariable Long id) {
        return service.loadQrImage(id, true).toResponseEntity();
    }
}
