package com.mineCryptos.controller;


import com.google.gson.Gson;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.model.entitities.enduser.CryptoDeposit;
import com.mineCryptos.model.entitities.enduser.DepositRequest;
import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;
import com.mineCryptos.service.Service.CryptoDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CryptoDepositController {
    private static final Logger logger = LoggerFactory.getLogger(CryptoDepositController.class);


    @Autowired
    private CryptoDepositService cryptoDepositService;


    @PostMapping("/deposit/create")
    public ResponseEntity<?> createDeposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(cryptoDepositService.createDeposit(request));
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


    @PostMapping("/deposit/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-nowpayments-sig", required = false) String signature) {

        logger.info("======== NOWPAYMENTS WEBHOOK RECEIVED ========");
        logger.info("Signature header: {}", signature);
        logger.info("Raw body: {}", rawBody);

        try {
            Map<String, Object> payload = new Gson().fromJson(rawBody, Map.class);
            cryptoDepositService.processWebhook(payload, signature, rawBody);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            logger.error("Webhook error: ", e);
            return ResponseEntity.status(400).body("Webhook failed");
        }
    }






}
