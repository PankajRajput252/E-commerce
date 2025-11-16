package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.admin.SubscriptionDefinition;

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

    FinalResponse confirmUser(String nodeId);

    FinalResponse getAdminDashboardCount(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse getSubscriptionDefinition(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addSubscriptionDefinition(SubscriptionDefinition subscriptionDefinition);

    FinalResponse updateSubscriptionDefinition(Integer id, SubscriptionDefinition subscriptionDefinition);

    FinalResponse deleteSubscriptionDefinition(Integer id);
}
