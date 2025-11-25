package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.model.entitities.enduser.DepositRequest;
import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;

import java.util.Map;

public interface CryptoDepositService {
    Map<String, Object> createDeposit(DepositRequest request);

//    void processWebhook(Map<String, Object> payload, String signature);

    FinalResponse getHistory(String userId);

    FinalResponse createBtcWithdrawal(String userNodeId, BtcWithdrawRequest request);

    void processWebhook(Map<String, Object> payload);

    FinalResponse confirmManually(String paymentId, String txHash);

//    void processWebhookRaw(String rawBody, String signature);

//     void processWebhook(Map<String, Object> body, String sig, String rawBody);
}
