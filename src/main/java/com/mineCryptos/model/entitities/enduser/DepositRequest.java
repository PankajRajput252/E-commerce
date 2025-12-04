package com.mineCryptos.model.entitities.enduser;

import javax.persistence.Transient;
import java.math.BigDecimal;

public class DepositRequest {
    private String userNodeId;
    private BigDecimal amount;
    private Integer depositPkId;
    private String selectedCurrency;   // INR / USDT / BTC / BNB


    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getDepositPkId() {
        return depositPkId;
    }

    public void setDepositPkId(Integer depositPkId) {
        this.depositPkId = depositPkId;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }
}
