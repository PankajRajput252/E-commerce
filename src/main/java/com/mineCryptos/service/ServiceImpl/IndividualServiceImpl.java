package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.IndividualIncomeSummary;
import com.mineCryptos.model.entitities.enduser.Wallet;
import com.mineCryptos.repo.enduser.IndividualIncomeSummaryRepository;
import com.mineCryptos.repo.enduser.WalletRepository;
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
}
