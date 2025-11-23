package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;
import com.mineCryptos.model.entitities.enduser.UserWallet;
import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.enduser.UserWalletRepository;
import com.mineCryptos.repo.enduser.WithdrawalRequestRepository;
import com.mineCryptos.service.Service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserWalletRepository userWalletRepository;
    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Override
    public FinalResponse createBtcWithdrawal(BtcWithdrawRequest request) {
        FinalResponse finalResponse = new FinalResponse();
        // 1. Validate User
        User user = userRepository.findByNodeIdAndActiveStateCodeFkId(request.getUserNodeId(),"ACTIVE");

        // 2. Validate KYC status
//        if (!user.isKycVerified()) {
//            throw new RuntimeException("KYC not completed");
//        }

        // 3. Validate wallet balance
        UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",request.getUserNodeId());
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
}
