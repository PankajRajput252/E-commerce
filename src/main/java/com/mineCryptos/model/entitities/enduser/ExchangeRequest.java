package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "exchange_request")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class ExchangeRequest extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXCHANGE_REQUEST_PK_ID")
    private Integer exchangeRequestPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "FROM_CURRENCY")
    private String fromCurrency ;

    @Column(name = "TO_CURRENCY")
    private String toCurrency ;

    @Column(name = "AMOUNT_BTC")
    private BigDecimal amountBTC;

    @Column(name = "RATE")
    private BigDecimal rate;

    @Column(name = "STATUS")
    private String status;

    public Integer getExchangeRequestPkId() {
        return exchangeRequestPkId;
    }

    public void setExchangeRequestPkId(Integer exchangeRequestPkId) {
        this.exchangeRequestPkId = exchangeRequestPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public BigDecimal getAmountBTC() {
        return amountBTC;
    }

    public void setAmountBTC(BigDecimal amountBTC) {
        this.amountBTC = amountBTC;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
