package com.mineCryptos.service.Service;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Role;
import com.mineCryptos.model.User;

public interface UserService {
    FinalResponse createUser(User user) throws FinalException;

    FinalResponse getUser(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException;
//
//    FinalResponse deleteUser(User user) throws FinalException;
//
//    FinalResponse updateUser(User user) throws FinalException;

    FinalResponse addRole(Role role);

    FinalResponse getRole();

     FinalResponse createRegistration(User user) throws FinalException;

    FinalResponse deleteUser(Integer id);

    FinalResponse updateUserStatus(Integer id, String activeStatusCode);
}
