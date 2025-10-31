package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "deposit_fund")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class DepositFund extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPOSIT_PK_ID")
    private Integer depositPkId;

    @Column(name = "USER_NODE_CODE")
    private String userNodeCode;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "STATUS")
    private String status; // PENDING, SUCCESS, FAILED

    @Column(name = "TRANSACTION_PASSWORD")
    private String transactionPassword;

    @Column(name = "CONFIRMED_AT")
    private LocalDateTime confirmedAt;

    @Transient
    private String userName;

    public Integer getDepositPkId() {
        return depositPkId;
    }

    public void setDepositPkId(Integer depositPkId) {
        this.depositPkId = depositPkId;
    }


    public String getUserNodeCode() {
        return userNodeCode;
    }

    public void setUserNodeCode(String userNodeCode) {
        this.userNodeCode = userNodeCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
