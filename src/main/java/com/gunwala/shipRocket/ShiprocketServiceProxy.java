package com.gunwala.shipRocket;

import com.gunwala.shipRocket.model.ShipRocketTokenResponse;
import com.gunwala.shipRocket.model.ShiprocketTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipRocket" , url = "https://apiv2.shiprocket.in/v1/external")
public interface ShiprocketServiceProxy {

    @GetMapping("/auth/login")
    ShipRocketTokenResponse generateOrderTokenResponse(@RequestBody ShiprocketTokenRequest shipRocketTokenRequest);
}
