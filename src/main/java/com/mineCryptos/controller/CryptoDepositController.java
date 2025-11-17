package com.mineCryptos.controller;


import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.CryptoDeposit;
import com.mineCryptos.model.entitities.enduser.DepositRequest;
import com.mineCryptos.service.Service.CryptoDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CryptoDepositController {

    @Autowired
    private CryptoDepositService cryptoDepositService;


    @PostMapping("/deposit/create")
    public ResponseEntity<?> createDeposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(cryptoDepositService.createDeposit(request));
    }

    @PostMapping("/deposit/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload,
                                     @RequestHeader("x-nowpayments-sig") String signature) {
        cryptoDepositService.processWebhook(payload, signature);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/deposit/history/{userId}")
    public FinalResponse getHistory(@PathVariable String userId) {
//        return depositRepo.findByUserId(userId);
        return  cryptoDepositService.getHistory(userId);
    }

}
