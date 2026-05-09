package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.UserVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserVisitRepo extends JpaRepository<UserVisit,Integer> {
    List<UserVisit> findByUserVisitPkId(Integer userVisitPkId);

    List<UserVisit> findByUserFkId(String userFkId);

    @Modifying
    @Query(value = "UPDATE user_visit SET USER_FK_ID=:userFkId,PRODUCT_FK_ID=:productFkId,VISITED_DATETIME=:visitedDateTime WHERE USER_VISIT_PK_ID=:userVisitPkId",nativeQuery = true)
    void updateUserVisit(Integer userVisitPkId, String userFkId, Integer productFkId, LocalDateTime visitedDateTime);

    List<UserVisit> findByProductFkId(Integer productFkId);
}
