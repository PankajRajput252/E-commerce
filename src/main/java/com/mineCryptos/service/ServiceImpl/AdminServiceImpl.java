package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.repo.admin.IncomeTypeRepository;
import com.mineCryptos.repo.admin.RankRewardRepository;
import com.mineCryptos.service.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RankRewardRepository rankRewardRepository;
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public FinalResponse getRankAndReward(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException {
        FinalResponse<RankReward> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<RankReward> rankRewardList = populateRankRewardView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateRankRewardViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(rankRewardList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateRankRewardViewCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = rankRewardRepository.countByRankIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = rankRewardRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<RankReward> populateRankRewardView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<RankReward> rankRewardList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            RankReward rankReward = rankRewardRepository.findByRankIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            rankRewardList.add(rankReward);
        } else {
            rankRewardList = rankRewardRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return rankRewardList;
    }

    @Override
    public FinalResponse addRankAndReward(RankReward rankReward) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        rankReward.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(rankReward.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        int count = rankRewardRepository.countByRankNameAndActiveStateCodeFkId(rankReward.getRankName(), "ACTIVE");
        if (count > 0) {
            Util.setMessage(finalResponse, "100", "Error: This Rank and reward already exists.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(rankReward);

        rankRewardRepository.save(rankReward);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    @Override
    public FinalResponse updateRankAndReward(Integer rankId, RankReward updatedRank) {
        FinalResponse finalResponse = new FinalResponse();
         rankRewardRepository.findById(rankId)
                .map(existing -> {
                    existing.setRankName(updatedRank.getRankName());
                    existing.setMatching(updatedRank.getMatching());
                    existing.setReward(updatedRank.getReward());
                    existing.setAchieved(updatedRank.isAchieved());
                    return rankRewardRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Rank not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
   public FinalResponse deleteRankAndReward(Integer id){
        FinalResponse finalResponse = new FinalResponse();
        rankRewardRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Override
    @Transactional(readOnly = true)
    public FinalResponse getIncomeType(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException {
        FinalResponse<IncomeType> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IncomeType> incomeTypeList = populateIncomeTypeView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateIncomeTypeCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(incomeTypeList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateIncomeTypeCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = incomeTypeRepository.countByIncomeTypePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = incomeTypeRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<IncomeType> populateIncomeTypeView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IncomeType> incomeTypeList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IncomeType incomeType = incomeTypeRepository.findByIncomeTypePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            incomeTypeList.add(incomeType);
        } else {
            incomeTypeList = incomeTypeRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return incomeTypeList;
    }

    @Override
    public FinalResponse addIncomeType(IncomeType incomeType) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        incomeType.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(incomeType.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(incomeType);

        incomeTypeRepository.save(incomeType);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    @Override
    public FinalResponse updateIncomeType(Integer rankId, IncomeType incomeType) {
        FinalResponse finalResponse = new FinalResponse();
        incomeTypeRepository.findById(rankId)
                .map(existing -> {
                    existing.setIncomeName(incomeType.getIncomeName());
                    existing.setPercentage(incomeType.getPercentage());
                    return incomeTypeRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("IncomeType not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteIncomeType(Integer id){
        FinalResponse finalResponse = new FinalResponse();
        incomeTypeRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

}
