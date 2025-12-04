package com.mineCryptos.controller;


import com.google.gson.Gson;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.model.entitities.enduser.CryptoDeposit;
import com.mineCryptos.model.entitities.enduser.DepositRequest;
import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;
import com.mineCryptos.service.Service.CryptoDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;



@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CryptoDepositController {
    private static final Logger logger = LoggerFactory.getLogger(CryptoDepositController.class);

    @Value("${nowpayments.ipn-secret}")
    private String ipnSecret;
    @Autowired
    private CryptoDepositService cryptoDepositService;


    @PostMapping("/deposit/create")
    public ResponseEntity<?> createDeposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(cryptoDepositService.createDepositV2(request));
    }
//
//    @PostMapping("/deposit/webhook")
//    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload,
//                                     @RequestHeader("x-nowpayments-sig") String signature) {
//        cryptoDepositService.processWebhook(payload, signature);
//        return ResponseEntity.ok("OK");
//    }

    @GetMapping("/deposit/history/{userId}")
    public FinalResponse getHistory(@PathVariable String userId) {
//        return depositRepo.findByUserId(userId);
        return  cryptoDepositService.getHistory(userId);
    }

    @PostMapping("/withdraw/withdrawBTC/{userNodeId}")
    public FinalResponse withdrawBTC(
            @RequestBody BtcWithdrawRequest request,
            @PathVariable String userNodeId
    ) {
       return cryptoDepositService.createBtcWithdrawal(userNodeId, request);
    }


//    @PostMapping("/deposit/webhook")
//    public ResponseEntity<?> webhook(
//            @RequestBody String rawBody,
//            @RequestHeader(value = "x-nowpayments-sig", required = false) String signature) {
//
//        System.out.println("RAW BODY: " + rawBody);
//        System.out.println("SIG: " + signature);
//
//        cryptoDepositService.processWebhookRaw(rawBody, signature);
//        return ResponseEntity.ok("OK");
//    }


//    @PostMapping("/deposit/webhook")
//    public ResponseEntity<String> webhook(
//            @RequestBody String rawBody,
//            @RequestHeader(value = "x-nowpayments-sig", required = false) String signature) {
//
//        logger.info("======== NOWPAYMENTS WEBHOOK RECEIVED ========");
//        logger.info("Signature header: {}", signature);
//        logger.info("Raw body: {}", rawBody);
//
//        // Check if rawBody is null
//        if (rawBody == null) {
//            logger.error("Raw body is null");
//            return ResponseEntity.badRequest().body("Raw body is required");
//        }
//
//        try {
//            Map<String, Object> payload = new Gson().fromJson(rawBody, Map.class);
//            cryptoDepositService.processWebhook(payload, signature, rawBody);
//            return ResponseEntity.ok("OK");
//        } catch (Exception e) {
//            logger.error("Webhook error: ", e);
//            return ResponseEntity.status(400).body("Webhook failed");
//        }
//    }


    @PostMapping("/deposit/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-nowpayments-sig", required = false) String signature) {

        logger.info("===== NOWPAYMENTS WEBHOOK RECEIVED =====");
        logger.info("Signature: {}", signature);
        logger.info("Raw body: {}", rawBody);

        try {
            if (signature == null) {
                logger.error("Missing signature");
                return ResponseEntity.status(400).body("Missing signature");
            }

            // Validate signature correctly
            if (!validateSignature(rawBody, signature)) {
                logger.error("Signature mismatch!");
                return ResponseEntity.status(400).body("Invalid signature");
            }

            // Convert to Map AFTER signature verification
            Map<String, Object> payload = new Gson().fromJson(rawBody, Map.class);

            cryptoDepositService.processWebhook(payload);
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            logger.error("Webhook error:", e);
            return ResponseEntity.status(400).body("Webhook failed");
        }
    }

    private boolean validateSignature(String rawBody, String receivedSignature) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(ipnSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(key);

            byte[] macData = sha512_HMAC.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = bytesToHex(macData);

            logger.info("Expected: {}", expectedSignature);
            logger.info("Received: {}", receivedSignature);

            return expectedSignature.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            logger.error("Signature validation error", e);
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @PostMapping("/confirmManually")
    FinalResponse confirmManually(
            @RequestParam(value = "paymentId") String paymentId,
            @RequestParam(value = "txHash", required = false) String txHash){
        return cryptoDepositService.confirmManually(paymentId, txHash);
    }


}
