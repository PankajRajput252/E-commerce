package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "business_history")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class BusinessHistory extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BUSINESS_HISTORY_PK_ID")
    private Integer businessHistoryPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;         // Who generated business

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PARENT_NODE_ID")
    private String parentNodeId;      // Upline who got benefit

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "TYPE")
    private String type;           // Subscription, Package Purchase, etc.

    @Column(name = "MODE")
    private String mode;           // Direct, Level, Team, etc.

    public Integer getBusinessHistoryPkId() {
        return businessHistoryPkId;
    }

    public void setBusinessHistoryPkId(Integer businessHistoryPkId) {
        this.businessHistoryPkId = businessHistoryPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
