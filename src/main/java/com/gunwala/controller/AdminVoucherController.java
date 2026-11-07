package com.gunwala.controller;

import com.gunwala.dto.VoucherRequest;
import com.gunwala.dto.VoucherResponse;
import com.gunwala.service.Service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin: voucher (coupon) codes. */
@RestController
@RequestMapping("/api/admin/vouchers")
@PreAuthorize("hasAuthority('ADMIN_USER')")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherService service;

    @GetMapping
    public List<VoucherResponse> list() {
        return service.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoucherResponse create(@Valid @RequestBody VoucherRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public VoucherResponse update(@PathVariable Long id, @Valid @RequestBody VoucherRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public VoucherResponse setActive(@PathVariable Long id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
