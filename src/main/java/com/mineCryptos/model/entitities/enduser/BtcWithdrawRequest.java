package com.mineCryptos.model.entitities.enduser;

import java.math.BigDecimal;

public class BtcWithdrawRequest {
    private BigDecimal amount;
    private String btcAddress;

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
}
