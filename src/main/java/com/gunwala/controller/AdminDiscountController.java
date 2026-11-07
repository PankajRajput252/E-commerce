package com.gunwala.controller;

import com.gunwala.dto.DiscountRequest;
import com.gunwala.dto.DiscountResponse;
import com.gunwala.service.Service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin: automatic sale discounts on store products. */
@RestController
@RequestMapping("/api/admin/discounts")
@PreAuthorize("hasAuthority('ADMIN_USER')")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService service;

    @GetMapping
    public List<DiscountResponse> list() {
        return service.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountResponse create(@Valid @RequestBody DiscountRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public DiscountResponse update(@PathVariable Long id, @Valid @RequestBody DiscountRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public DiscountResponse setActive(@PathVariable Long id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
