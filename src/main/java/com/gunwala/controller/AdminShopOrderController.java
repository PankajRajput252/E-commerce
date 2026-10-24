package com.gunwala.controller;

import com.gunwala.dto.PageResponse;
import com.gunwala.dto.ShopOrderResponse;
import com.gunwala.entity.OrderStatus;
import com.gunwala.entity.ShippingStatus;
import com.gunwala.service.Service.ShiprocketOrderPlacementService;
import com.gunwala.service.Service.ShopOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/** Admin: see paid orders and their Shiprocket status; retry a failed shipment. */
@RestController
@RequestMapping("/api/admin/shop-orders")
@RequiredArgsConstructor
public class AdminShopOrderController {

    private final ShopOrderService orderService;
    private final ShiprocketOrderPlacementService placementService;

    /** e.g. GET /api/admin/shop-orders?status=PAID&shippingStatus=FAILED */
    @GetMapping
    public PageResponse<ShopOrderResponse> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) ShippingStatus shippingStatus,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.search(status, shippingStatus, pageable);
    }

    @GetMapping("/{orderReference}")
    public ShopOrderResponse get(@PathVariable String orderReference) {
        return orderService.getForAdmin(orderReference);
    }

    /**
     * Runs the remaining Shiprocket steps now (synchronously) and returns the result.
     * 409 if the order isn't paid, needs manual handling, or is already being processed.
     */
    @PostMapping("/{orderReference}/ship")
    public ShopOrderResponse ship(@PathVariable String orderReference) {
        return placementService.placeShipment(orderReference, true);
    }
}
