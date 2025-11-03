package com.mineCryptos.controller;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getWalletData(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getIndividualIncomeSummary(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getIndividualMiningPackage(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getIndividualDepositFund(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getWalletTransaction(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
        if (Util.isDefined(inputFkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getSupportTicket(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
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
}
