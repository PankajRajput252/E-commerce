package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.DepositRequest;

import java.util.Map;

public interface CryptoDepositService {
    Map<String, Object> createDeposit(DepositRequest request);

    void processWebhook(Map<String, Object> payload, String signature);

    FinalResponse getHistory(String userId);
}
