package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.IndividualIncomeSummary;
import com.mineCryptos.model.entitities.enduser.Wallet;

public interface IndividualService {
    FinalResponse getWalletData(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWalletData(Wallet wallet);

    FinalResponse updateWalletData(Integer id, Wallet wallet);

    FinalResponse deleteWalletData(Integer id);

    FinalResponse getIndividualIncomeSummary(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addIndividualIncomeSummary(IndividualIncomeSummary individualIncomeSummary);

    FinalResponse updateIndividualIncomeSummary(Integer id, IndividualIncomeSummary individualIncomeSummary);

    FinalResponse deleteIndividualIncomeSummary(Integer id);
}
