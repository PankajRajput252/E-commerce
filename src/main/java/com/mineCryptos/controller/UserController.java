package com.mineCryptos.controller;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Role;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000/")
@CrossOrigin("*")
@RestController
    @RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    //creating user
    @PostMapping("/addUser")
    public FinalResponse createUser(@RequestBody User user) throws FinalException {
        return this.userService.createUser(user);

    }

    @GetMapping("/getRole")
    public FinalResponse getRole() throws FinalException {
        return this.userService.getRole();

    }


    @PostMapping("/addRole")
    public FinalResponse addRole(@RequestBody Role role) throws FinalException {
        return this.userService.addRole(role);

    }

    @GetMapping("/getUser")
    public FinalResponse viewUser(
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
        return userService.getUser(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
    }
//    @PutMapping("/updateUser")
//    public FinalResponse updateUser(
//            @RequestBody User user) throws FinalException {
//        return this.userService.updateUser(user);
//    }
//
//
//    @DeleteMapping("/deleteUser")
//    FinalResponse deleteUser(@RequestBody User user) throws FinalException {
//        return this.userService.deleteUser(user);
//    }



}
