package com.gunwala.service.Service;

import com.gunwala.FinalException;
import com.gunwala.model.FinalResponse;
import com.gunwala.model.Role;
import com.gunwala.model.User;
import com.gunwala.model.entitities.gunwala.UserInfo;

public interface UserService {
    FinalResponse createUser(User user) throws FinalException;

    FinalResponse getUser(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException;
//
//    FinalResponse deleteUser(User user) throws FinalException;
//
//    FinalResponse updateUser(User user) throws FinalException;

    FinalResponse addRole(Role role);

    FinalResponse getRole();

     FinalResponse createRegistration(User user) throws FinalException;

    FinalResponse deleteUser(Integer id);

    FinalResponse updateUserStatus(Integer id, String activeStatusCode);

    FinalResponse updateUserProfile(UserInfo userInfo);
}
