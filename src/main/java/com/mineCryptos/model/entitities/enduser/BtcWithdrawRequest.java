package com.mineCryptos.model.entitities.enduser;

import java.math.BigDecimal;

public class BtcWithdrawRequest {
    private BigDecimal amount;
    private String btcAddress;
    private String userNodeId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBtcAddress() {
        return btcAddress;
    }

    public void setBtcAddress(String btcAddress) {
        this.btcAddress = btcAddress;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }
}
