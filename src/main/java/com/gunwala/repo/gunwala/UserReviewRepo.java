package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserReviewRepo extends JpaRepository<UserReview,Integer> {
    List<UserReview> findByUserReviewPkId(Integer userReviewPkId);

    List<UserReview> findByUserFkId(String userFkId);

    List<UserReview> findByProductFkId(Integer productFkId);

    @Modifying
    @Query(value = "UPDATE TABLE user_review SET USER_FK_ID=:userFkId,PRODUCT_FK_ID=:productFkId,COMMENT=:comment,CREATED_DATETIME=:createdDateTime WHERE USER_REVIEW_PK_ID=:userReviewPkId",nativeQuery = true)
    void updateUserReview(Integer userReviewPkId, String userFkId, Integer productFkId, String comment, LocalDateTime createdDateTime);
}
