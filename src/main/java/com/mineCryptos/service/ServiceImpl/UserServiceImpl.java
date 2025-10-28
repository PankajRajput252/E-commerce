package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.*;
import com.mineCryptos.repo.RoleRepository;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //creating user
    @Override
    public FinalResponse createUser(User user) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        //effective date cannot be greater than present date
        if (Util.compareDate(user.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        int count = userRepository.countByUserPkIdAndActiveStateCodeFkId(user.getUserPkId(),  "ACTIVE");
        if (count>0) {
            Util.setMessage(finalResponse, "100", "Error: This User already exists.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse addRole(Role role) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        roleRepository.save(role);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public FinalResponse getUser( Integer inputPkId, Integer inputFkId, int page, int size, String filterBy,String searchValue) throws FinalException {
        FinalResponse<User> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<User> userList = populateUserView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateUserViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(userList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateUserViewCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = userRepository.countByUserPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = userRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<User> populateUserView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<User> userList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            User user = userRepository.findByUserPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            userList.add(user);
        } else {
            userList = userRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return userList;
    }


//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public FinalResponse deleteUser( User user) throws FinalException {
//       FinalResponse finalResponse=new FinalResponse();
//        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
//        //Dirty update
//        String parsedLastModifiedDateTime = Util.parseDateTimeAsLocalDateTime(String.valueOf(user.getLastModifiedDateTime()));
//        Long count = userRepository.dirtyUpdateCheck(user.getUserPkId(), parsedLastModifiedDateTime, "ACTIVE");
//        if (count != 1) {
//            Util.setMessage(finalResponse, "100", "Error: Looks like somebody else has updated your record while you were contemplating your action. Refresh and then redo what you intended to do");
//            return finalResponse;
//        }
//        user.setEffectiveDateTime(vLastModifiedDateTime);
//        user.setIsDeleted(Boolean.TRUE);
//        commitChangesForUser( user);
//        Util.setSuccessMessage(finalResponse);
//        return finalResponse;
//    }

//    @Override
//    public User updateUser(User user) throws Exception {
//        Optional<User> existingUser=userRepository.findById(user.getId());
//        if(!existingUser.isPresent())
//        {
//            throw new Exception("Id is not present");
//        }
//        else
//        {
//            existingUser.get().setFirstName(user.getFirstName());
//            user=userRepository.save(user);
//        }
//        return  user;
////    }
//   @Override
//    public FinalResponse updateUser(User user) throws Exception {
//        Optional<User> existingUser = userRepository.findById(user.getUserId());
//        if(!existingUser.isPresent()) {
//            throw new Exception("Some Error Occured While updating the user");
//        }
//        else {
//            existingUser.get().setName(user.getName());
//            user = userRepository.save(user);
//        }
//        return user;
//    }
//
//    @Override
//    public FinalResponse updateUser(User user) throws FinalException {
//        FinalResponse finalResponse = new FinalResponse();
//        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
//        //effective date cannot be greater than present date
//        if (Util.compareDate(user.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
//            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
//            return finalResponse;
//        }
//        //Dirty update
//        String parsedLastModifiedDateTime = Util.parseDateTimeAsLocalDateTime(String.valueOf(user.getLastModifiedDateTime()));
//        Long count = userRepository.dirtyUpdateCheck(user.getUserPkId(), parsedLastModifiedDateTime, "ACTIVE");
//        if (count != 1) {
//           Util.setMessage(finalResponse, "100", "Error: Looks like somebody else has updated your record while you were contemplating your action. Refresh and then redo what you intended to do");
//            return finalResponse;
//        }
//
//        user.setEffectiveDateTime(vLastModifiedDateTime);
//
//        commitChangesForUser(user);
//
//        finalResponse= Util.setSuccessMessage(finalResponse);
//        return finalResponse;
//    }
//
//    private void commitChangesForUser(User user) {
//        UUID previousVersionId = user.getVersionId();
//        Util.setCommonDefaultAttributes(user);
//        // insert new version with existingPk
//        insertNewRecordForUser(user);
//        //update Previous version to history
//        updatePreviousVersionToHistoryForUser(previousVersionId);
//    }
//
//    private void insertNewRecordForUser(User user) {
//        UUID previousPkId = user.getUserPkId();
//        // set pkId to null , since JPA will create new record only if pk id passed is null , later on we will set old pkId
//        user.setUserPkId(null);
//        //creates new record in db
//        userRepository.save(user);
//        // as we want new record to have new versionId and existing Pk id , so update new pk ID with existing pkId
//        userRepository.updateNewRecordWithPreviousPkId(previousPkId, user.getVersionId());
//    }
//
//    private void updatePreviousVersionToHistoryForUser(UUID previousVersionId) {
//        userRepository.updatePreviousVersionToHistory(LocalDateTime.now(), previousVersionId);
//    }

    @Override
    public FinalResponse getRole(){
        FinalResponse finalResponse=new FinalResponse();
       List<Role>roles= roleRepository.findAll();
       finalResponse.setData(roles);
       return finalResponse;
    }


    @Override
    public FinalResponse createRegistration(User user) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        user.setEffectiveDateTime(vLastModifiedDateTime);
        Util.setCommonDefaultAttributes(user);
        // generating nodeId and validating

        String nodeId;
        do {
           nodeId=Util.generateNodeId();
        } while (userRepository.existsByNodeId(nodeId));

        user.setNodeId(nodeId);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        if(Util.isDefined(user.getIsUserIsAdmin())) {
            Role role = this.roleRepository.findById(FinalConstants.ADMIN_USER).get();
            user.getRoles().add(role);
        }
        else{
            Role role = this.roleRepository.findById(FinalConstants.NORMAL_USER).get();
            user.getRoles().add(role);
        }
        userRepository.save(user);
        finalResponse = Util.setSuccessMessage(finalResponse);
        finalResponse.setMessage(nodeId);
        return finalResponse;
    }

}
