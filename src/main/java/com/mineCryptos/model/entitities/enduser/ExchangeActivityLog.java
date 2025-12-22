package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "exchange_activity_log")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class ExchangeActivityLog extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EXCHANGE_ACTIVITY_LOG_PK_ID")
    private  Integer exchangeActivityLogPkId;

    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @Column(name = "ACTION_BY")
    private String actionBy;

    @Column(name = "ACTION_TYPE")
    private String actionType;

    @Column(name = "MESSAGE")
    private String message;

    @Transient
    private ExchangeRequest exchangeRequest;

    public Integer getExchangeActivityLogPkId() {
        return exchangeActivityLogPkId;
    }

    public void setExchangeActivityLogPkId(Integer exchangeActivityLogPkId) {
        this.exchangeActivityLogPkId = exchangeActivityLogPkId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExchangeRequest getExchangeRequest() {
        return exchangeRequest;
    }

    public void setExchangeRequest(ExchangeRequest exchangeRequest) {
        this.exchangeRequest = exchangeRequest;
    }
}
