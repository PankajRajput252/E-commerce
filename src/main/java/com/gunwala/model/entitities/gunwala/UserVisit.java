package com.gunwala.model.entitities.gunwala;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_visit")
public class UserVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="USER_VISIT_PK_ID")
    private Integer userVisitPkId;

    @Column(name="USER_FK_ID")
    private String userFkId;

    @Column(name="PRODUCT_FK_ID")
    private Integer productFkId;

    @Column(name="VISITED_DATETIME ")
    private LocalDateTime visitedDateTime;

    public Integer getUserVisitFkId() {
        return userVisitPkId;
    }

    public Integer getUserVisitPkId() {
        return userVisitPkId;
    }

    public void setUserVisitPkId(Integer userVisitPkId) {
        this.userVisitPkId = userVisitPkId;
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

    public LocalDateTime getVisitedDateTime() {
        return visitedDateTime;
    }

    public void setVisitedDateTime(LocalDateTime visitedDateTime) {
        this.visitedDateTime = visitedDateTime;
    }

}
