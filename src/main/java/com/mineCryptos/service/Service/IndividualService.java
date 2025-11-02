package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.*;

public interface IndividualService {
    FinalResponse getWalletData(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWalletData(Wallet wallet);

    FinalResponse updateWalletData(Integer id, Wallet wallet);

    FinalResponse deleteWalletData(Integer id);

    FinalResponse getIndividualIncomeSummary(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addIndividualIncomeSummary(IndividualIncomeSummary individualIncomeSummary);

    FinalResponse updateIndividualIncomeSummary(Integer id, IndividualIncomeSummary individualIncomeSummary);

    FinalResponse deleteIndividualIncomeSummary(Integer id);

    FinalResponse getIndividualMiningPackage(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addMiningPackage(MiningPackage miningPackage);

    FinalResponse updateMiningPackage(Integer id, MiningPackage miningPackage);

    FinalResponse deleteMiningPackage(Integer id);

    FinalResponse getIndividualDepositFund(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addDepositFund(DepositFund depositFund);

    FinalResponse updateDepositFund(Integer id, DepositFund depositFund);

    FinalResponse deleteDepositFund(Integer id);

    FinalResponse getWalletTransaction(Integer inputPkIdInt, Integer inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWalletTransaction(WalletTransaction walletTransaction);

    FinalResponse updateWalletTransaction(Integer id, WalletTransaction walletTransaction);

    FinalResponse deleteWalletTransaction(Integer id);

    FinalResponse getAllDirectMember(String inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse getTeamHierarchy(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse updateProfile(Profile profile);
}
