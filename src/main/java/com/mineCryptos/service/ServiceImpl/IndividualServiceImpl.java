package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.repo.enduser.*;
import com.mineCryptos.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndividualServiceImpl implements IndividualService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private IndividualIncomeSummaryRepository individualIncomeSummaryRepository;
    @Autowired
    private MiningPackageRepository miningPackageRepository;
    @Autowired
    private DepositFundRepository depositFundRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public FinalResponse getWalletData(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<Wallet> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<Wallet> walletList = populateWalletView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateWalletViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(walletList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateWalletViewCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = walletRepository.countByWalletPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = walletRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<Wallet> populateWalletView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<Wallet> walletList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            Wallet wallet = walletRepository.findByWalletPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            walletList.add(wallet);
        } else {
            walletList = walletRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return walletList;
    }


    @Override
    public FinalResponse addWalletData(Wallet wallet) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        wallet.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(wallet.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(wallet);

        walletRepository.save(wallet);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateWalletData(Integer walletId, Wallet wallet) {
        FinalResponse finalResponse = new FinalResponse();
        walletRepository.findById(walletId)
                .map(existing -> {
                    existing.setMineWallet(wallet.getMineWallet());
                    existing.setNodeWallet(wallet.getNodeWallet());
                    existing.setCapitalWallet(wallet.getCapitalWallet());
                    existing.setTotalCredit(wallet.getTotalCredit());
                    existing.setTotalDebit(wallet.getTotalDebit());
                    return walletRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Wallet not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteWalletData(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        walletRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualIncomeSummary(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<IndividualIncomeSummary> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IndividualIncomeSummary> individualIncomeSummaryList = populateIndividualIncomeSummaryView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateIndividualIncomeSummaryCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(individualIncomeSummaryList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateIndividualIncomeSummaryCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = individualIncomeSummaryRepository.countByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = individualIncomeSummaryRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<IndividualIncomeSummary> populateIndividualIncomeSummaryView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IndividualIncomeSummary> individualIncomeSummaryList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IndividualIncomeSummary individualIncomeSummary = individualIncomeSummaryRepository.findByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            individualIncomeSummaryList.add(individualIncomeSummary);
        } else {
            individualIncomeSummaryList = individualIncomeSummaryRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return individualIncomeSummaryList;
    }
    @Override
    public FinalResponse addIndividualIncomeSummary(IndividualIncomeSummary individualIncomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        individualIncomeSummary.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(individualIncomeSummary.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(individualIncomeSummary);

        individualIncomeSummaryRepository.save(individualIncomeSummary);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateIndividualIncomeSummary(Integer id, IndividualIncomeSummary individualIncomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        individualIncomeSummaryRepository.findById(id)
                .map(existing -> {
                    existing.setServiceGenerationAmount(individualIncomeSummary.getServiceGenerationAmount());
                    existing.setMatchingIncomeAmount(individualIncomeSummary.getMatchingIncomeAmount());
                    existing.setClubIncomeAmount(individualIncomeSummary.getClubIncomeAmount());
                    existing.setRewardIncomeAmount(individualIncomeSummary.getRewardIncomeAmount());
                    existing.setFastTrackBonusAmount(individualIncomeSummary.getFastTrackBonusAmount());
                    existing.setMiningProfitSharingAmount(individualIncomeSummary.getMiningProfitSharingAmount());
                    existing.setMiningGenerationIncomeAmount(individualIncomeSummary.getMiningGenerationIncomeAmount());
                    existing.setNodeBusinessSharingAmount(individualIncomeSummary.getNodeBusinessSharingAmount());
                    return individualIncomeSummaryRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Individual income  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteIndividualIncomeSummary(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        individualIncomeSummaryRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualMiningPackage(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MiningPackage> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MiningPackage> miningPackageList = populateMiningPackageView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateMiningPackageCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(miningPackageList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateMiningPackageCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = miningPackageRepository.countByMiningPackagePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = miningPackageRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<MiningPackage> populateMiningPackageView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<MiningPackage> miningPackageList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            MiningPackage miningPackage = miningPackageRepository.findByMiningPackagePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            miningPackageList.add(miningPackage);
        } else {
            miningPackageList = miningPackageRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return miningPackageList;
    }


    @Override
    public FinalResponse addMiningPackage(MiningPackage miningPackage) {
        FinalResponse finalResponse=new FinalResponse();
        if (miningPackage.getPackageAmount() < 100 || miningPackage.getPackageAmount() % 10 != 0) {
            throw new IllegalArgumentException("Amount must be >= 100 and in multiples of 10");
        }

        // 2. Validate transaction password and OTP
        // (Assume validateTransactionPassword() and validateOtp() are implemented)
//        if (!validateTransactionPassword(request.getUserId(), request.getTransactionPassword())) {
//            throw new IllegalArgumentException("Invalid transaction password");
//        }
//
//        if (!validateOtp(request.getUserId(), request.getOtp())) {
//            throw new IllegalArgumentException("Invalid OTP");
//        }

         miningPackageRepository.save(miningPackage);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private boolean validateTransactionPassword(String userId, String password) {
        // TODO: integrate with user auth table
        return "mySecurePass".equals(password);
    }

    private boolean validateOtp(String userId, String otp) {
        // TODO: integrate OTP validation logic
        return "894512".equals(otp);
    }

    @Override
    public FinalResponse updateMiningPackage(Integer id, MiningPackage miningPackage) {
        FinalResponse finalResponse = new FinalResponse();
        miningPackageRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeCode(miningPackage.getUserNodeCode());
                    existing.setPackageAmount(miningPackage.getPackageAmount());
                    existing.setRemarks(miningPackage.getRemarks());
                    existing.setMode(miningPackage.getMode());
                    existing.setTransactionPassword(miningPackage.getTransactionPassword());
                    return miningPackageRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Mining package   not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteMiningPackage(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        miningPackageRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualDepositFund(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<DepositFund> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<DepositFund> depositFundList = populateDepositFundView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateDepositFundCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(depositFundList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateDepositFundCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = depositFundRepository.countByDepositPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = depositFundRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<DepositFund> populateDepositFundView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<DepositFund> depositFundList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            DepositFund depositFund = depositFundRepository.findByDepositPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            depositFundList.add(depositFund);
        } else {
            depositFundList = depositFundRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return depositFundList;
    }

    @Override
    public FinalResponse addDepositFund(DepositFund depositFund) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        depositFund.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(depositFund.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(depositFund);

        depositFundRepository.save(depositFund);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateDepositFund(Integer id, DepositFund depositFund) {
        FinalResponse finalResponse = new FinalResponse();
        depositFundRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeCode(depositFund.getUserNodeCode());
                    existing.setCurrency(depositFund.getCurrency());
                    existing.setAmount(depositFund.getAmount());
                    existing.setStatus(depositFund.getStatus());
                    existing.setTransactionPassword(depositFund.getTransactionPassword());
                    existing.setConfirmedAt(depositFund.getConfirmedAt());
                    return depositFundRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Mining package   not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteDepositFund(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        depositFundRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getWalletTransaction(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<WalletTransaction> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<WalletTransaction> walletTransactionList = populateWalletTransactionView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateWalletTransactionCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(walletTransactionList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateWalletTransactionCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = walletTransactionRepository.countByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = walletTransactionRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<WalletTransaction> populateWalletTransactionView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<WalletTransaction> walletTransactionList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            WalletTransaction walletTransaction = walletTransactionRepository.findByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            walletTransactionList.add(walletTransaction);
        } else {
            walletTransactionList = walletTransactionRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return walletTransactionList;
    }
    @Override
    public FinalResponse addWalletTransaction(WalletTransaction walletTransaction) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        walletTransaction.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(walletTransaction.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(walletTransaction);

        walletTransactionRepository.save(walletTransaction);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateWalletTransaction(Integer id, WalletTransaction walletTransaction) {
        FinalResponse finalResponse = new FinalResponse();
        walletTransactionRepository.findById(id)
                .map(existing -> {
                    existing.setTransactionId(walletTransaction.getTransactionId());
                    existing.setFromUserId(walletTransaction.getFromUserId());
                    existing.setToUserId(walletTransaction.getToUserId());
                    existing.setFromWallet(walletTransaction.getFromWallet());
                    existing.setToWallet(walletTransaction.getToWallet());
                    existing.setAmount(walletTransaction.getAmount());
                    existing.setStatus(walletTransaction.getStatus());
                    existing.setRemarks(walletTransaction.getRemarks());
                    existing.setConfirmedAt(walletTransaction.getConfirmedAt());
                    return walletTransactionRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Wallet Txn    not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteWalletTransaction(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        walletTransactionRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


}
