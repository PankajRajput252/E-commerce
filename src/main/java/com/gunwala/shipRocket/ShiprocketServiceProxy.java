package com.gunwala.shipRocket;

import com.gunwala.shipRocket.model.OrderRequestBody;
import com.gunwala.shipRocket.model.OrderResponse;
import com.gunwala.shipRocket.model.ShipRocketTokenResponse;
import com.gunwala.shipRocket.model.ShiprocketTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "shipRocket" , url = "https://apiv2.shiprocket.in/v1/external")
public interface ShiprocketServiceProxy {

    @GetMapping("/auth/login")
    ShipRocketTokenResponse generateOrderTokenResponse(@RequestBody ShiprocketTokenRequest shipRocketTokenRequest);

    @PostMapping("/orders/create/adhoc")
    OrderResponse createShiprocketOrderResponse(@RequestHeader Map<String, String> headerMap, @RequestBody OrderRequestBody orderRequestBody);
}
