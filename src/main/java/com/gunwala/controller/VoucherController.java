package com.gunwala.controller;

import com.gunwala.dto.ValidateVoucherRequest;
import com.gunwala.dto.VoucherQuoteResponse;
import com.gunwala.service.Service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/** Buyer: check a voucher code against the cart. Requires login (POST is not public in SecurityConfig). */
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService service;

    @PostMapping("/validate")
    public VoucherQuoteResponse validate(@Valid @RequestBody ValidateVoucherRequest request, Principal user) {
        return service.quote(user.getName(), request);
    }
}
