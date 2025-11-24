package com.mineCryptos.controller;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.service.Service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/withdraw")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;


    // User initiates
    @PostMapping("/btc")
    public FinalResponse withdrawBTC(
            @RequestBody BtcWithdrawRequest request
    ) {
        return withdrawalService.createWithdrawal(request);
    }

    // Admin approves
    @PostMapping("/approve/{withdrawalRequestPkId}")
    public FinalResponse approve(@PathVariable Integer withdrawalRequestPkId) {
        return withdrawalService.approveWithdrawal(withdrawalRequestPkId);
    }


    // System/admin triggers processing (or use scheduler)
    @PostMapping("/process/{withdrawalRequestPkId}")
    public FinalResponse process(@PathVariable Integer withdrawalRequestPkId) {
        return withdrawalService.processWithdrawal(withdrawalRequestPkId);
    }

    @PutMapping("/reject/{withdrawalRequestPkId}")
    public FinalResponse reject(@PathVariable Integer withdrawalRequestPkId) {
        return withdrawalService.reject(withdrawalRequestPkId);
    }
}
