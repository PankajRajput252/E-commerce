package com.gunwala.shipRocket;

import com.gunwala.shipRocket.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "shipRocket" , url = "https://apiv2.shiprocket.in/v1/external")
public interface ShiprocketServiceProxy {


    @GetMapping("/auth/login")
    ShipRocketTokenResponse generateOrderTokenResponse(@RequestBody ShiprocketTokenRequest shipRocketTokenRequest);

    @PostMapping("/orders/create/adhoc")
    OrderResponse createShiprocketOrderResponse(@RequestHeader Map<String, String> headerMap, @RequestBody OrderRequestBody orderRequestBody);

    @GetMapping("/courier/track/shipment/{shipment_id}")
    TrackingReaponse trackShipment(String shipmentId, @RequestHeader Map<String, String> headerMap) ;

    @PostMapping("/courier/assign/awb")
    AwbResponseAssaign generateAwb(@RequestHeader Map<String, String> headerMap,@RequestBody AwbRequestBody awbRequestBody);

    @PostMapping("/v1/external/courier/generate/pickup")
    PickupResponse generatePickup(Map<String, String> headers,@RequestParam String shipmentId);
}

