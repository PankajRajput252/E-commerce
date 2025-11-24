package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;

public interface WithdrawalService {
    FinalResponse createWithdrawal(BtcWithdrawRequest request);

    FinalResponse approveWithdrawal(Integer withdrawalRequestPkId);

    FinalResponse processWithdrawal(Integer id);

    FinalResponse reject(Integer withdrawalRequestPkId);
}
