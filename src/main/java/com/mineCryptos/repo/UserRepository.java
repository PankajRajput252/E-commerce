package com.mineCryptos.repo;

import com.mineCryptos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    int countByUserPkIdAndActiveStateCodeFkId(int userPkId, String active);

    User findByUserPkIdAndActiveStateCodeFkId(int inputPkId, String active);

    List<User> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByActiveStateCodeFkId(String active);

//    //    Dirty update check for other than add
//    @Query(value = "SELECT count(*) FROM users  WHERE USER_PK_ID=?1 AND LAST_MODIFIED_DATETIME=?2 AND SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID=?3 AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0", nativeQuery = true)
//    Long dirtyUpdateCheck(UUID pkId, String lastModifiedDateTime, String activeState);
//
//    @Modifying
//    @Query("update User u set u.userPkId = ?1 where u.versionId = ?2")
//    public void updateNewRecordWithPreviousPkId(UUID pkId, UUID versionId);
//
//    @Modifying
//    @Query("update #{#entityName} u set u.recordStateCodeFkId = 'HISTORY' ,u.lastModifiedDateTime = ?1  where u.versionId = ?2")
//    public void updatePreviousVersionToHistory(LocalDateTime lastModifiedDateTime, UUID versionId);

    Optional<User> findByEmail(String email);

    boolean existsByNodeId(String nodeId);

    User findByNodeIdAndActiveStateCodeFkId(String username, String active);
}
