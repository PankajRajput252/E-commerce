package com.mineCryptos.repo;

import com.mineCryptos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
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
    List<User> findByParentNodeIdAndActiveStateCodeFkId(String username, String active, Pageable pageable);
    User findByParentNodeIdAndActiveStateCodeFkId(String username, String active);

    @Query("SELECT t.name FROM User t where t.nodeId = ?1 And t.activeStateCodeFkId =?2")
    String fetchUserNameBasedOnNodeId(String userNodeCode, String active);

    List<User> findByActiveStateCodeFkIdAndParentNodeIdContaining(String active, String searchValue, Pageable pageable);

    int countByParentNodeIdAndActiveStateCodeFkId(String inputPkId, String active);

    int countByActiveStateCodeFkIdAndParentNodeIdContaining(String active, String searchValue);


    @Transactional
    @Modifying
    @Query("update User u set u.imageId = ?1 where u.nodeId = ?2")
    void updateProfileImageUrlBasedOnNodeId(String imageId, String nodeId);

    @Query("SELECT t.parentNodeId FROM User t where t.nodeId = ?1 And t.activeStateCodeFkId =?2")
    String fetchParentNodeBasedOnUserNodeId(String userNodeCode, String active);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    int countByRoleName(@Param("roleName") String roleName);

    List<User> findByActiveStateCodeFkIdAndNodeId(String filterBy, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndNodeId(String filterBy, String inputFkId);

    int countByEmailAndActiveStateCodeFkId(String email, String active);

    int countByMobileAndActiveStateCodeFkId(String email, String active);

    @Query(value = " WITH RECURSIVE downline AS (  "+
       " SELECT node_id  "+
      "  FROM users  "+
      "  WHERE parent_node_id = :userId  "+
     "   UNION ALL "+
     "   SELECT u.node_id  "+
      "  FROM users u  "+
       " INNER JOIN downline d ON u.parent_node_id = d.node_id "+
   " )  "+
  "  SELECT COUNT(*) FROM downline " , nativeQuery = true)
    int countTotalTeam(@Param("userId") String userId);

    @Query(value = " WITH RECURSIVE left_team AS (\n" +
            "    SELECT node_id\n" +
            "    FROM users\n" +
            "    WHERE parent_node_id = :userId\n" +
            "      AND position = 'Left'\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT u.node_id\n" +
            "    FROM users u\n" +
            "    INNER JOIN left_team lt ON u.parent_node_id = lt.node_id\n" +
            ")\n" +
            "SELECT COUNT(*)\n" +
            "FROM users u\n" +
            "WHERE u.node_id IN (SELECT node_id FROM left_team)\n" +
            "  AND u.user_status = 'ACTIVE' ",nativeQuery = true)
    int totalLeftTeam(@Param("userId") String userId);

    @Query(value = " WITH RECURSIVE right_team AS (\n" +
            "    SELECT node_id\n" +
            "    FROM users\n" +
            "    WHERE parent_node_id = :userId\n" +
            "      AND position = 'Right'\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT u.node_id\n" +
            "    FROM users u\n" +
            "    INNER JOIN right_team rt ON u.parent_node_id = rt.node_id\n" +
            ")\n" +
            "SELECT COUNT(*)\n" +
            "FROM users u\n" +
            "WHERE u.node_id IN (SELECT node_id FROM right_team)\n" +
            "  AND u.user_status = 'ACTIVE' ",nativeQuery = true)
    int totalRightTeam(@Param("userId") String userId);
}
