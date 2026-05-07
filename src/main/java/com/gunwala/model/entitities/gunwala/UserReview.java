package com.gunwala.model.entitities.gunwala;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_review")
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_REVIEW_PK_ID")
    private Integer userReviewPkId;

    @Column(name = "USER_FK_ID")
    private String userFkId;

    @Column(name = "PRODUCT_FK_ID")
    private Integer productFkId;

    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createdDateTime;

    @Transient
    private String userName;

    public Integer getUserReviewPkId() {
        return userReviewPkId;
    }

    public void setUserReviewPkId(Integer userReviewPkId) {
        this.userReviewPkId = userReviewPkId;
    }

    public String getUserFkId() {
        return userFkId;
    }

    public void setUserFkId(String userFkId) {
        this.userFkId = userFkId;
    }

    public Integer getProductFkId() {
        return productFkId;
    }

    public void setProductFkId(Integer productFkId) {
        this.productFkId = productFkId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }


}