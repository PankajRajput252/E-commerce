package com.gunwala.controller;


import com.gunwala.service.ServiceImpl.ShiprocketShipmentService;
import com.gunwala.shipRocket.model.AwbResponseAssaign;
import com.gunwala.shipRocket.model.PickupResponse;
import com.gunwala.shipRocket.model.TrackingReaponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shiprocket")
@CrossOrigin(origins = "*")
public class ShiprocketController {

    @Autowired
    private ShiprocketShipmentService shipmentService;

    // ─── Track shipment — used by frontend "Track Order" page ────────────────
    // GET /api/shiprocket/track/{shipmentId}
    @GetMapping("/track/{shipmentId}")
    public ResponseEntity<?> trackShipment(@PathVariable String shipmentId) {
        try {
            TrackingReaponse response = shipmentService.trackShipment(shipmentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }

    // ─── Manual retry — admin can re-trigger AWB/pickup if it failed ─────────
    // POST /api/shiprocket/generate-awb?shipmentId=xxx&courierId=
    @PostMapping("/generate-awb")
    public ResponseEntity<?> generateAwb(
            @RequestParam String shipmentId,
            @RequestParam(defaultValue = "") String courierId
    ) {
        try {
            AwbResponseAssaign response = shipmentService.generateAwb(shipmentId, courierId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }

    // ─── Manual retry — admin can re-trigger pickup ──────────────────────────
    // POST /api/shiprocket/generate-pickup?shipmentId=xxx
    @PostMapping("/generate-pickup")
    public ResponseEntity<?> generatePickup(@RequestParam String shipmentId) {
        try {
            PickupResponse response = shipmentService.generatePickup(shipmentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }
}