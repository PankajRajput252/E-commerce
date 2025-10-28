package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "eu_individual_income_summary")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class IndividualIncomeSummary extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDIVIDUAL_INCOME_SUMMARY_PK_ID")
    private Integer individualIncomeSummaryPkId;

    @Column(name = "SERVICE_GENERATION_AMOUNT")
    private double serviceGenerationAmount;

    @Column(name = "MATCHNING_INCOME_AMOUNT")
    private double matchingIncomeAmount;

    @Column(name = "CLUB_INCOME_AMOUNT")
    private double clubIncomeAmount;

    @Column(name = "REWARD_INCOME_AMOUNT")
    private double rewardIncomeAmount;

    @Column(name = "FAST_TRACK_BONUS_AMOUNT")
    private double fastTrackBonusAmount;

    @Column(name = "MINING_PROFIT_SHARING_AMOUNT")
    private double miningProfitSharingAmount;

    @Column(name = "MINING_GENERATION_INCOME_AMOUNT")
    private double miningGenerationIncomeAmount;

    @Column(name = "NODE_BUSINESS_SHARING_AMOUNT")
    private double nodeBusinessSharingAmount;

    @Column(name = "USER_FK_ID")
    private  Integer userFkId;

    public IndividualIncomeSummary() {
        this.serviceGenerationAmount = 0.0;
        this.matchingIncomeAmount = 0.0;
        this.clubIncomeAmount = 0.0;
        this.rewardIncomeAmount = 0.0;
        this.fastTrackBonusAmount = 0.0;
        this.miningProfitSharingAmount = 0.0;
        this.miningGenerationIncomeAmount = 0.0;
        this.nodeBusinessSharingAmount = 0.0;
    }

    public IndividualIncomeSummary(Integer individualIncomeSummaryPkId, double serviceGenerationAmount, double matchingIncomeAmount, double clubIncomeAmount, double rewardIncomeAmount, double fastTrackBonusAmount, double miningProfitSharingAmount, double miningGenerationIncomeAmount, double nodeBusinessSharingAmount) {
        this.individualIncomeSummaryPkId = individualIncomeSummaryPkId;
        this.serviceGenerationAmount = serviceGenerationAmount;
        this.matchingIncomeAmount = matchingIncomeAmount;
        this.clubIncomeAmount = clubIncomeAmount;
        this.rewardIncomeAmount = rewardIncomeAmount;
        this.fastTrackBonusAmount = fastTrackBonusAmount;
        this.miningProfitSharingAmount = miningProfitSharingAmount;
        this.miningGenerationIncomeAmount = miningGenerationIncomeAmount;
        this.nodeBusinessSharingAmount = nodeBusinessSharingAmount;
    }

    public Integer getIndividualIncomeSummaryPkId() {
        return individualIncomeSummaryPkId;
    }

    public void setIndividualIncomeSummaryPkId(Integer individualIncomeSummaryPkId) {
        this.individualIncomeSummaryPkId = individualIncomeSummaryPkId;
    }

    public double getServiceGenerationAmount() {
        return serviceGenerationAmount;
    }

    public void setServiceGenerationAmount(double serviceGenerationAmount) {
        this.serviceGenerationAmount = serviceGenerationAmount;
    }

    public double getMatchingIncomeAmount() {
        return matchingIncomeAmount;
    }

    public void setMatchingIncomeAmount(double matchingIncomeAmount) {
        this.matchingIncomeAmount = matchingIncomeAmount;
    }

    public double getClubIncomeAmount() {
        return clubIncomeAmount;
    }

    public void setClubIncomeAmount(double clubIncomeAmount) {
        this.clubIncomeAmount = clubIncomeAmount;
    }

    public double getRewardIncomeAmount() {
        return rewardIncomeAmount;
    }

    public void setRewardIncomeAmount(double rewardIncomeAmount) {
        this.rewardIncomeAmount = rewardIncomeAmount;
    }

    public double getFastTrackBonusAmount() {
        return fastTrackBonusAmount;
    }

    public void setFastTrackBonusAmount(double fastTrackBonusAmount) {
        this.fastTrackBonusAmount = fastTrackBonusAmount;
    }

    public double getMiningProfitSharingAmount() {
        return miningProfitSharingAmount;
    }

    public void setMiningProfitSharingAmount(double miningProfitSharingAmount) {
        this.miningProfitSharingAmount = miningProfitSharingAmount;
    }

    public double getMiningGenerationIncomeAmount() {
        return miningGenerationIncomeAmount;
    }

    public void setMiningGenerationIncomeAmount(double miningGenerationIncomeAmount) {
        this.miningGenerationIncomeAmount = miningGenerationIncomeAmount;
    }

    public double getNodeBusinessSharingAmount() {
        return nodeBusinessSharingAmount;
    }

    public void setNodeBusinessSharingAmount(double nodeBusinessSharingAmount) {
        this.nodeBusinessSharingAmount = nodeBusinessSharingAmount;
    }

    public Integer getUserFkId() {
        return userFkId;
    }

    public void setUserFkId(Integer userFkId) {
        this.userFkId = userFkId;
    }
}
