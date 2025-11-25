package com.mineCryptos.controller;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.service.Service.CryptoDepositService;
import com.mineCryptos.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/individual")
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @GetMapping("/getWalletData")
    public FinalResponse getRankAndReward(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return individualService.getWalletData(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addWalletData")
    public FinalResponse addRankAndReward(@RequestBody Wallet wallet) throws FinalException {
        return this.individualService.addWalletData(wallet);
    }

    @PutMapping("/updateWalletData/{id}")
    public FinalResponse updateRankAndReward(@PathVariable Integer id, @RequestBody Wallet wallet) {
        return individualService.updateWalletData(id, wallet);
    }

    @DeleteMapping("/deleteWalletData/{id}")
    public FinalResponse deleteRankAndReward(@PathVariable Integer id) {
        return individualService.deleteWalletData(id);
    }


    @GetMapping("/getIndividualIncomeSummary")
    public FinalResponse getIncomeType(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getIndividualIncomeSummary(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addIndividualIncomeSummary")
    public FinalResponse addIncomeType(@RequestBody IndividualIncomeSummary individualIncomeSummary) throws FinalException {
        return this.individualService.addIndividualIncomeSummary(individualIncomeSummary);
    }

    @PutMapping("/updateIndividualIncomeSummary/{id}")
    public FinalResponse updateIncomeType(@PathVariable Integer id, @RequestBody IndividualIncomeSummary individualIncomeSummary) {
        return individualService.updateIndividualIncomeSummary(id, individualIncomeSummary);
    }

    @DeleteMapping("/deleteIndividualIncomeSummary/{id}")
    public FinalResponse deleteIncomeType(@PathVariable Integer id) {
        return individualService.deleteIndividualIncomeSummary(id);
    }


    @GetMapping("/getIndividualMiningPackage")
    public FinalResponse getIndividualMiningPackage(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
//        Integer inputFkIdInt = null;
//        if (Util.isDefined(inputPkId)) {
//            inputPkIdInt = Util.convertStringToInteger(inputPkId);
//        }
        return individualService.getIndividualMiningPackage(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addMiningPackage")
    public FinalResponse addMiningPackage(@RequestBody MiningPackage miningPackage) throws FinalException {
        return this.individualService.addMiningPackage(miningPackage);
    }

    @PutMapping("/updateMiningPackage/{id}")
    public FinalResponse updateMiningPackage(@PathVariable Integer id, @RequestBody MiningPackage miningPackage) {
        return individualService.updateMiningPackage(id, miningPackage);
    }


    @DeleteMapping("/deleteMiningPackage/{id}")
    public FinalResponse deleteMiningPackage(@PathVariable Integer id) {
        return individualService.deleteMiningPackage(id);
    }

    @GetMapping("/getIndividualDepositFund")
    public FinalResponse getIndividualDepositFund(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return individualService.getIndividualDepositFund(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addDepositFund")
    public FinalResponse addDepositFund(@RequestBody DepositFund depositFund) throws FinalException {
        return this.individualService.addDepositFund(depositFund);
    }

    @PutMapping("/updateDepositFund/{id}")
    public FinalResponse updateDepositFund(@PathVariable Integer id, @RequestBody DepositFund depositFund) {
        return individualService.updateDepositFund(id, depositFund);
    }

    @DeleteMapping("/deleteDepositFund/{id}")
    public FinalResponse deleteDepositFund(@PathVariable Integer id) {
        return individualService.deleteDepositFund(id);
    }


    @GetMapping("/getWalletTransaction")
    public FinalResponse getWalletTransaction(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
//        if (Util.isDefined(inputPkId)) {
//            inputPkIdInt = Util.convertStringToInteger(inputPkId);
//        }
        return individualService.getWalletTransaction(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addWalletTransaction")
    public FinalResponse addWalletTransaction(@RequestBody WalletTransaction walletTransaction) throws FinalException {
        return this.individualService.addWalletTransaction(walletTransaction);
    }

    @PutMapping("/updateWalletTransaction/{id}")
    public FinalResponse updateWalletTransaction(@PathVariable Integer id, @RequestBody WalletTransaction walletTransaction) {
        return individualService.updateWalletTransaction(id, walletTransaction);
    }

    @DeleteMapping("/deleteWalletTransaction/{id}")
    public FinalResponse deleteWalletTransaction(@PathVariable Integer id) {
        return individualService.deleteWalletTransaction(id);
    }

    @GetMapping("/getAllDirectMember")
    public FinalResponse getAllDirectMember(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        return individualService.getAllDirectMember(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @GetMapping("/getTeamHierarchy")
    public FinalResponse getTeamHierarchy(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        return individualService.getTeamHierarchy(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @PutMapping("/updateProfile")
    public FinalResponse updateProfile(@RequestBody Profile profile) {
        return individualService.updateProfile(profile);
    }

    @GetMapping("/getSupportTicket")
    public FinalResponse getSupportTicket(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return individualService.getSupportTicket(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addSupportTicket")
    public FinalResponse addSupportTicket(@RequestBody SupportTicket supportTicket) throws FinalException {
        return this.individualService.addSupportTicket(supportTicket);
    }

    @PutMapping("/updateSupportTicket/{id}")
    public FinalResponse updateSupportTicket(@PathVariable Integer id, @RequestBody SupportTicket supportTicket) {
        return individualService.updateSupportTicket(id, supportTicket);
    }

    @DeleteMapping("/deleteSupportTicket/{id}")
    public FinalResponse deleteSupportTicket(@PathVariable Integer id) {
        return individualService.deleteSupportTicket(id);
    }

    @GetMapping("/getWithdrawalRequest")
    public FinalResponse getWithdrawalRequest(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getWithdrawalRequest(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addWithDrawalRequest")
    public FinalResponse addWithDrawalRequest(@RequestBody WithdrawalRequest withdrawalRequest) throws FinalException {
        return this.individualService.addWithDrawalRequest(withdrawalRequest);
    }

    @PutMapping("/updateWithDrawalRequest/{id}")
    public FinalResponse updateWithDrawalRequest(@PathVariable Integer id, @RequestBody WithdrawalRequest withdrawalRequest) {
        return individualService.updateWithDrawalRequest(id, withdrawalRequest);
    }

    @DeleteMapping("/deleteWithDrawalRequest/{id}")
    public FinalResponse deleteWithDrawalRequest(@PathVariable Integer id) {
        return individualService.deleteWithDrawalRequest(id);
    }


    @GetMapping("/getCommissionLedger")
    public FinalResponse getCommissionLedger(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getCommissionLedger(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addCommissionLedger")
    public FinalResponse addCommissionLedger(@RequestBody CommissionLedger commissionLedger) throws FinalException {
        return this.individualService.addCommissionLedger(commissionLedger);
    }

    @PutMapping("/updateCommissionLedger/{id}")
    public FinalResponse updateCommissionLedger(@PathVariable Integer id, @RequestBody  CommissionLedger commissionLedger) {
        return individualService.updateCommissionLedger(id, commissionLedger);
    }

    @DeleteMapping("/deleteCommissionLedger/{id}")
    public FinalResponse deleteCommissionLedger(@PathVariable Integer id) {
        return individualService.deleteCommissionLedger(id);
    }

    @GetMapping("/getIncomeSummary")
    public FinalResponse getIncomeSummary(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getIncomeSummary(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addIncomeSummary")
    public FinalResponse addIncomeSummary(@RequestBody IncomeSummary incomeSummary) throws FinalException {
        return this.individualService.addIncomeSummary(incomeSummary);
    }

    @PutMapping("/updateIncomeSummary/{id}")
    public FinalResponse updateIncomeSummary(@PathVariable Integer id, @RequestBody  IncomeSummary incomeSummary) {
        return individualService.updateIncomeSummary(id, incomeSummary);
    }

    @DeleteMapping("/deleteIncomeSummary/{id}")
    public FinalResponse deleteIncomeSummary(@PathVariable Integer id) {
        return individualService.deleteIncomeSummary(id);
    }

    @GetMapping("/getAccountStatement")
    public FinalResponse getAccountStatement(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getAccountStatement(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addAccountStatement")
    public FinalResponse addAccountStatement(@RequestBody AccountStatement accountStatement) throws FinalException {
        return this.individualService.addAccountStatement(accountStatement);
    }

    @PutMapping("/updateAccountStatement/{id}")
    public FinalResponse updateAccountStatement(@PathVariable Integer id, @RequestBody  AccountStatement accountStatement) {
        return individualService.updateAccountStatement(id, accountStatement);
    }

    @DeleteMapping("/deleteAccountStatement/{id}")
    public FinalResponse deleteAccountStatement(@PathVariable Integer id) {
        return individualService.deleteAccountStatement(id);
    }

    @GetMapping("/getBussinessHistory")
    public FinalResponse getBussinessHistory(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getBussinessHistory(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addBusinessHistory")
    public FinalResponse addBusinessHistory(@RequestBody BusinessHistory businessHistory) throws FinalException {
        return this.individualService.addBusinessHistory(businessHistory);
    }

    @PutMapping("/updateBusinessHistory/{id}")
    public FinalResponse updateBusinessHistory(@PathVariable Integer id, @RequestBody  BusinessHistory businessHistory) {
        return individualService.updateBusinessHistory(id, businessHistory);
    }

    @DeleteMapping("/deleteBusinessHistory/{id}")
    public FinalResponse deleteBusinessHistory(@PathVariable Integer id) {
        return individualService.deleteBusinessHistory(id);
    }

    @GetMapping("/getIndividualRankReward")
    public FinalResponse getIndividualRankReward(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        return individualService.getIndividualRankReward(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addIndividualRankReward")
    public FinalResponse addIndividualRankReward(@RequestBody IndividualRankReward individualRankReward) throws FinalException {
        return this.individualService.addIndividualRankReward(individualRankReward);
    }

    @PutMapping("/updateIndividualRankReward/{id}")
    public FinalResponse updateIndividualRankReward(@PathVariable Integer id, @RequestBody  IndividualRankReward businessHistory) {
        return individualService.updateIndividualRankReward(id, businessHistory);
    }

    @DeleteMapping("/deleteIndividualRankReward/{id}")
    public FinalResponse deleteIndividualRankReward(@PathVariable Integer id) {
        return individualService.deleteIndividualRankReward(id);
    }


    @GetMapping("/getCryptoDepositSummary")
    public FinalResponse getCryptoDepositSummary(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {

        return individualService.getCryptoDepositSummary(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addCryptoDeposit")
    public FinalResponse addCryptoDeposit(@RequestBody CryptoDeposit cryptoDeposit) throws FinalException {
        return this.individualService.addCryptoDeposit(cryptoDeposit);
    }

    @PutMapping("/updateCryptoDeposit/{id}")
    public FinalResponse updateCryptoDeposit(@PathVariable Integer id, @RequestBody CryptoDeposit cryptoDeposit) {
        return individualService.updateCryptoDeposit(id, cryptoDeposit);
    }

    @DeleteMapping("/deleteCryptoDeposit/{id}")
    public FinalResponse deleteCryptoDeposit(@PathVariable Integer id) {
        return individualService.deleteCryptoDeposit(id);
    }

    @GetMapping("/getHierarchy")
    public FinalResponse getCryptoDepositSummary(
            @RequestParam(value = "loggedInNodeId", required = false) String loggedInNodeId
    ) throws FinalException {

        return individualService.getHierarchy(loggedInNodeId);
    }



    @GetMapping("/getUserWalletAddress")
    public FinalResponse getUserWalletAddress(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return individualService.getUserWalletAddress(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/addUserWalletAddress")
    public FinalResponse addUserWalletAddress(@RequestBody UserWalletAddress userWalletAddress) throws FinalException {
        return this.individualService.addUserWalletAddress(userWalletAddress);
    }

    @PutMapping("/updateUserWalletAddress/{id}")
    public FinalResponse updateUserWalletAddress(@PathVariable Integer id, @RequestBody UserWalletAddress userWalletAddress) {
        return individualService.updateUserWalletAddress(id, userWalletAddress);
    }

    @DeleteMapping("/deleteUserWalletAddress/{id}")
    public FinalResponse deleteUserWalletAddress(@PathVariable Integer id) {
        return individualService.deleteUserWalletAddress(id);
    }

    @GetMapping("/getBusinessDetails")
    public FinalResponse getBusinessDetails(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {

        return individualService.getBusinessDetails(inputPkId, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping("/sendEmailOtp")
    public FinalResponse sendEmailOtp(@RequestParam String email,
                                      @RequestParam String userNodeId) throws FinalException {
        String otp = individualService.generateAndSave(email,userNodeId);
     return  individualService.sendOtp(email, otp);
    }

    @GetMapping("/getOtpForVerification")
    public FinalResponse getOtpForVerification(
            @RequestParam(value = "userNodeId") String userNodeId
    ) throws FinalException {
        return individualService.getOtpForVerification(userNodeId);
    }

    @GetMapping("/getRankMaster")
    public FinalResponse getRankMaster(
    ) throws FinalException {
        return individualService.getRankMaster();
    }

}
