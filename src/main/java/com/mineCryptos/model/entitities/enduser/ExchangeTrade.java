package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "exchange_trade")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class ExchangeTrade   extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EXCHANGE_TRADE_PK_ID")
    private  Integer exchangeTradePkId;

    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @Column(name = "SELLER_ID")
    private String sellerId;

    @Column(name = "BUYER_ID")
    private String buyerId;

    @Column(name = "BUYER_HOLD_AMOUNT")
    private BigDecimal buyerHoldAmount;

    @Column(name = "EXCHANGE_STATUS")
    private String exchangeStatus;  // PENDING','PAID','CONFIRMED','DISPUTED','COMPLETED') DEFAULT 'PENDING',

    public Integer getExchangeTradePkId() {
        return exchangeTradePkId;
    }

    public void setExchangeTradePkId(Integer exchangeTradePkId) {
        this.exchangeTradePkId = exchangeTradePkId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public BigDecimal getBuyerHoldAmount() {
        return buyerHoldAmount;
    }

    public void setBuyerHoldAmount(BigDecimal buyerHoldAmount) {
        this.buyerHoldAmount = buyerHoldAmount;
    }

    public String getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(String exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }
}
