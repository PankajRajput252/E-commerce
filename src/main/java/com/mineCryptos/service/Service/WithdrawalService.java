package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.BtcWithdrawRequest;

public interface WithdrawalService {
    FinalResponse createBtcWithdrawal(BtcWithdrawRequest request);
}
