package com.mineCryptos.model.entitities.enduser;

import javax.persistence.Transient;

public class DepositRequest {
    private String userNodeId;
    private Double amount;
    private Integer depositPkId;

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getDepositPkId() {
        return depositPkId;
    }

    public void setDepositPkId(Integer depositPkId) {
        this.depositPkId = depositPkId;
    }
}
