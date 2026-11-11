package com.gunwala.controller;

import com.gunwala.dto.CartItemRequest;
import com.gunwala.dto.CartResponse;
import com.gunwala.service.Service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Buyer's saved cart (login required — see SecurityConfig). Every call returns the whole cart
 * with live prices, so the website can just replace its copy with the response.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @GetMapping
    public CartResponse get(Principal user) {
        return service.get(user.getName());
    }

    /** Add to cart: { productId, quantity } — quantity is added to what's already there. */
    @PostMapping("/items")
    public CartResponse add(@Valid @RequestBody CartItemRequest request, Principal user) {
        return service.add(user.getName(), request.productId(), request.quantity());
    }

    /** Change a line's quantity: { quantity }. */
    @PutMapping("/items/{productId}")
    public CartResponse setQuantity(@PathVariable Integer productId,
                                    @Valid @RequestBody CartItemRequest request,
                                    Principal user) {
        return service.setQuantity(user.getName(), productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse remove(@PathVariable Integer productId, Principal user) {
        return service.remove(user.getName(), productId);
    }

    @DeleteMapping
    public CartResponse clear(Principal user) {
        return service.clear(user.getName());
    }
}
