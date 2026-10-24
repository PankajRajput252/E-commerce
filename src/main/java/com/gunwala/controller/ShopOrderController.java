package com.gunwala.controller;

import com.gunwala.dto.CreateShopOrderRequest;
import com.gunwala.dto.ShopOrderResponse;
import com.gunwala.service.Service.ShopOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/** Buyer: create a checkout order (address + items) before paying by bank / UPI, and track it. */
@RestController
@RequestMapping("/api/shop-orders")
@RequiredArgsConstructor
public class ShopOrderController {

    private final ShopOrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopOrderResponse create(@Valid @RequestBody CreateShopOrderRequest request, Principal user) {
        return service.create(user.getName(), request);
    }

    /** Orders that are paid, or have a payment submitted (abandoned checkouts are left out). */
    @GetMapping
    public List<ShopOrderResponse> myOrders(Principal user) {
        return service.listForUser(user.getName());
    }

    @GetMapping("/{orderReference}")
    public ShopOrderResponse get(@PathVariable String orderReference, Principal user) {
        return service.getForUser(orderReference, user.getName());
    }
}
