package com.mineCryptos.controller;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.IndividualIncomeSummary;
import com.mineCryptos.model.entitities.enduser.Wallet;
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
}
