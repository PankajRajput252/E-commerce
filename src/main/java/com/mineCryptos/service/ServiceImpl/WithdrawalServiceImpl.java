package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.enduser.BinanceWithdrawResponseDto;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.model.entitities.enduser.UserWallet;
import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.enduser.UserWalletRepository;
import com.mineCryptos.repo.enduser.WalletRepository;
import com.mineCryptos.repo.enduser.WithdrawalRequestRepository;
import com.mineCryptos.service.Service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserWalletRepository userWalletRepository;
    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;
    @Autowired
    private BinanceClient binanceClient;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public FinalResponse createWithdrawal(BtcWithdrawRequest request) {
        FinalResponse finalResponse = new FinalResponse();
        // 1. Validate User
        User user = userRepository.findByNodeIdAndActiveStateCodeFkId(request.getUserNodeId(), "ACTIVE");

        // 2. Validate KYC status
//        if (!user.isKycVerified()) {
//            throw new RuntimeException("KYC not completed");
//        }

        // 3. Validate wallet balance
        UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE", request.getUserNodeId());
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 4. Calculate fee (example: 0.0003 BTC)
        BigDecimal networkFee = new BigDecimal("0.0003");
        BigDecimal finalAmount = request.getAmount().subtract(networkFee);

        if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount too low after fee deduction");
        }

        // 5. Freeze funds
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setFrozenBalance(wallet.getFrozenBalance().add(request.getAmount()));
        userWalletRepository.save(wallet);

        // 6. Create Withdrawal Request
        WithdrawalRequest withdrawal = new WithdrawalRequest();
        withdrawal.setUserNodeId(request.getUserNodeId());
        withdrawal.setCurrencyCode("BTC");
        withdrawal.setAmount(request.getAmount());
        withdrawal.setFeeWork(networkFee);
        withdrawal.setFinalAmount(finalAmount);
        withdrawal.setWalletAddress(request.getBtcAddress());
        withdrawal.setStatus("PENDING");
        withdrawalRequestRepository.save(withdrawal);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Transactional
    public FinalResponse approveWithdrawal(Integer withdrawalId) {
        FinalResponse finalResponse = new FinalResponse();
        WithdrawalRequest req = withdrawalRequestRepository.findById(withdrawalId).orElseThrow(() -> new RuntimeException("Withdrawal not found"));
        if (!"PENDING".equals(req.getStatus())) throw new RuntimeException("Invalid status");
        req.setStatus("APPROVED");
        req.setUpdatedAtDateTime(LocalDateTime.now());
         withdrawalRequestRepository.save(req);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Transactional
    public FinalResponse processWithdrawal(Integer withdrawalId) {
        FinalResponse finalResponse=new FinalResponse();
        WithdrawalRequest req = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new RuntimeException("Withdrawal not found"));

        if (!"APPROVED".equals(req.getStatus()))
            throw new RuntimeException("Only approved withdrawals can be processed");

        // mark processing
        req.setStatus("PROCESSING");
        withdrawalRequestRepository.save(req);

        try {

            // 🚀 Generic withdrawal call
            BinanceWithdrawResponseDto resp = binanceClient.withdraw(
                    req.getCurrencyCode(),
                    req.getWalletAddress(),
                    req.getWalletType(),
                    req.getFinalAmount().toPlainString()
            );

            // update success
            req.setStatus("SUCCESS");
            req.setTxId(resp.getId());
            req.setUpdatedAtDateTime(LocalDateTime.now());
            withdrawalRequestRepository.save(req);

            // deduct frozen funds
            UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",req.getUserNodeId());
            wallet.setFrozenBalance(wallet.getFrozenBalance().subtract(req.getAmount()));
            userWalletRepository.save(wallet);

            finalResponse.setResponse(resp);
            finalResponse = Util.setSuccessMessage(finalResponse);
            return finalResponse;

        } catch (Exception ex) {

            // mark failed
            req.setStatus("FAILED");
            req.setUpdatedAtDateTime(LocalDateTime.now());
            withdrawalRequestRepository.save(req);

            // refund user
            refund(req);

            throw new RuntimeException("Withdrawal processing failed: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public void refund(WithdrawalRequest req) {
        UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE", req.getUserNodeId());
        wallet.setBalance(wallet.getBalance().add(req.getAmount()));
        wallet.setFrozenBalance(wallet.getFrozenBalance().subtract(req.getAmount()));
        userWalletRepository.save(wallet);
    }
}
