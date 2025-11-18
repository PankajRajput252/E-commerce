package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class WalletTransaction extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WALLET_TXN_PK_ID")
    private Integer walletTxnPkId;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "FROM_USER_ID")
    private String fromUserId;

    @Column(name = "TO_USER_ID")
    private String toUserId;

    @Column(name = "T0_WALLET")
    private String toWallet;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "STATUS")
    private String status; // SUCCESS / FAILED

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CONFIRMED_AT")
    private LocalDateTime confirmedAt;

    @Transient
    private String userName;

    public Integer getWalletTxnPkId() {
        return walletTxnPkId;
    }

    public void setWalletTxnPkId(Integer walletTxnPkId) {
        this.walletTxnPkId = walletTxnPkId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToWallet() {
        return toWallet;
    }

    public void setToWallet(String toWallet) {
        this.toWallet = toWallet;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
