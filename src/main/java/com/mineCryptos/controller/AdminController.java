package com.mineCryptos.controller;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.service.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
   private AdminService adminService;

    @GetMapping("/getRankAndReward")
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
        return adminService.getRankAndReward(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
    }

    @PostMapping("/addRankAndReward")
    public FinalResponse addRankAndReward(@RequestBody RankReward rankReward) throws FinalException {
        return this.adminService.addRankAndReward(rankReward);
    }

    @PutMapping("/updateRankAndReward/{id}")
    public FinalResponse updateRankAndReward(@PathVariable Integer id, @RequestBody RankReward rankReward) {
        return adminService.updateRankAndReward(id, rankReward);
    }

    @DeleteMapping("/deleteRankAndReward/{id}")
    public FinalResponse deleteRankAndReward(@PathVariable Integer id) {
        return adminService.deleteRankAndReward(id);
    }


    @GetMapping("/getIncomeType")
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
        return adminService.getIncomeType(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
    }

    @PostMapping("/addIncomeType")
    public FinalResponse addIncomeType(@RequestBody IncomeType incomeType) throws FinalException {
        return this.adminService.addIncomeType(incomeType);
    }

    @PutMapping("/updateIncomeType/{id}")
    public FinalResponse updateIncomeType(@PathVariable Integer id, @RequestBody IncomeType incomeType) {
        return adminService.updateIncomeType(id, incomeType);
    }

    @DeleteMapping("/deleteIncomeType/{id}")
    public FinalResponse deleteIncomeType(@PathVariable Integer id) {
        return adminService.deleteIncomeType(id);
    }
}
