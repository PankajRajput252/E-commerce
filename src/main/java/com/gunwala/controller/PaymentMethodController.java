package com.gunwala.controller;

import com.gunwala.dto.PaymentMethodResponse;
import com.gunwala.service.Service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** End user: see which payment options are available (bank account and/or QR). */
@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService service;

    @GetMapping
    public List<PaymentMethodResponse> listActive() {
        return service.listActive();
    }

    @GetMapping("/{id}/qr-image")
    public ResponseEntity<Resource> qrImage(@PathVariable Long id) {
        return service.loadQrImage(id, false).toResponseEntity();
    }
}
