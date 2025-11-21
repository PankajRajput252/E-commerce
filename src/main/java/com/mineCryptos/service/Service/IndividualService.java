package com.mineCryptos.service.Service;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.entitities.enduser.*;

public interface IndividualService {
    FinalResponse getWalletData(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWalletData(Wallet wallet);

    FinalResponse updateWalletData(Integer id, Wallet wallet);

    FinalResponse deleteWalletData(Integer id);

    FinalResponse getIndividualIncomeSummary(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addIndividualIncomeSummary(IndividualIncomeSummary individualIncomeSummary);

    FinalResponse updateIndividualIncomeSummary(Integer id, IndividualIncomeSummary individualIncomeSummary);

    FinalResponse deleteIndividualIncomeSummary(Integer id);

    FinalResponse getIndividualMiningPackage(String inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addMiningPackage(MiningPackage miningPackage);

    FinalResponse updateMiningPackage(Integer id, MiningPackage miningPackage);

    FinalResponse deleteMiningPackage(Integer id);

    FinalResponse getIndividualDepositFund(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addDepositFund(DepositFund depositFund);

    FinalResponse updateDepositFund(Integer id, DepositFund depositFund);

    FinalResponse deleteDepositFund(Integer id);

    FinalResponse getWalletTransaction(String inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWalletTransaction(WalletTransaction walletTransaction);

    FinalResponse updateWalletTransaction(Integer id, WalletTransaction walletTransaction);

    FinalResponse deleteWalletTransaction(Integer id);

    FinalResponse getAllDirectMember(String inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse getTeamHierarchy(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse updateProfile(Profile profile);

    FinalResponse getSupportTicket(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addSupportTicket(SupportTicket supportTicket);

    FinalResponse updateSupportTicket(Integer id, SupportTicket supportTicket);

    FinalResponse deleteSupportTicket(Integer id);

    FinalResponse getWithdrawalRequest(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse addWithDrawalRequest(WithdrawalRequest withdrawalRequest);

    FinalResponse updateWithDrawalRequest(Integer id, WithdrawalRequest withdrawalRequest);

    FinalResponse deleteWithDrawalRequest(Integer id);

    FinalResponse getCommissionLedger(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addCommissionLedger(CommissionLedger commissionLedger);

    FinalResponse updateCommissionLedger(Integer id, CommissionLedger commissionLedger);

    FinalResponse deleteCommissionLedger(Integer id);

    FinalResponse getIncomeSummary(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addIncomeSummary(IncomeSummary incomeSummary);

    FinalResponse updateIncomeSummary(Integer id, IncomeSummary incomeSummary);

    FinalResponse deleteIncomeSummary(Integer id);

    FinalResponse getAccountStatement(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addAccountStatement(AccountStatement accountStatement);

    FinalResponse updateAccountStatement(Integer id, AccountStatement accountStatement);

    FinalResponse deleteAccountStatement(Integer id);

    FinalResponse getBussinessHistory(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addBusinessHistory(BusinessHistory businessHistory);

    FinalResponse updateBusinessHistory(Integer id, BusinessHistory businessHistory);

    FinalResponse deleteBusinessHistory(Integer id);

    FinalResponse getIndividualRankReward(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addIndividualRankReward(IndividualRankReward individualRankReward);

    FinalResponse updateIndividualRankReward(Integer id, IndividualRankReward businessHistory);

    FinalResponse deleteIndividualRankReward(Integer id);

    FinalResponse getCryptoDepositSummary(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addCryptoDeposit(CryptoDeposit cryptoDeposit);

    FinalResponse updateCryptoDeposit(Integer id, CryptoDeposit cryptoDeposit);

    FinalResponse deleteCryptoDeposit(Integer id);

    public FinalResponse getHierarchy(String loggedInNodeId);

    FinalResponse deleteUserWalletAddress(Integer id);

    FinalResponse updateUserWalletAddress(Integer id, UserWalletAddress userWalletAddress);

    FinalResponse addUserWalletAddress(UserWalletAddress userWalletAddress);

    FinalResponse getUserWalletAddress(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue);
}
