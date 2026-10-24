package com.gunwala.shipRocket;

import com.gunwala.shipRocket.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Shiprocket API v1 (https://apidocs.shiprocket.in).
 * Fixed: login is a POST; tracking needs @PathVariable; pickup URL had "/v1/external" twice and
 * its headers/body were not annotated, so Feign sent the header map as the request body.
 */
@FeignClient(name = "shipRocket", url = "${shiprocket.base-url:https://apiv2.shiprocket.in/v1/external}")
public interface ShiprocketServiceProxy {

    @PostMapping("/auth/login")
    ShipRocketTokenResponse generateOrderTokenResponse(@RequestBody ShiprocketTokenRequest shipRocketTokenRequest);

    @PostMapping("/orders/create/adhoc")
    OrderResponse createShiprocketOrderResponse(@RequestHeader Map<String, String> headerMap,
                                                @RequestBody OrderRequestBody orderRequestBody);

    @GetMapping("/courier/track/shipment/{shipment_id}")
    TrackingReaponse trackShipment(@PathVariable("shipment_id") String shipmentId,
                                   @RequestHeader Map<String, String> headerMap);

    @PostMapping("/courier/assign/awb")
    AwbResponseAssaign generateAwb(@RequestHeader Map<String, String> headerMap,
                                   @RequestBody AwbRequestBody awbRequestBody);

    @PostMapping("/courier/generate/pickup")
    PickupResponse generatePickup(@RequestHeader Map<String, String> headerMap,
                                  @RequestBody PickupRequestBody pickupRequestBody);
}
