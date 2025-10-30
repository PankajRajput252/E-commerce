package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;

public interface AdminService {
    FinalResponse getRankAndReward(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addRankAndReward(RankReward rankReward);

    FinalResponse updateRankAndReward(Integer id, RankReward rankReward);

    FinalResponse deleteRankAndReward(Integer id);

    FinalResponse getIncomeType(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addIncomeType(IncomeType incomeType);

    FinalResponse updateIncomeType(Integer id, IncomeType incomeType);

    FinalResponse deleteIncomeType(Integer id);

    FinalResponse confirmDeposit(Integer depositId);

    FinalResponse confirmWalletTransaction(Integer depositId);
}
