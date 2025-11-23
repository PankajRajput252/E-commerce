package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_request")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class WithdrawalRequest extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WITHDRAWAL_REQUEST_PK_ID")
    private  Integer withdrawalRequestPkId;

    @Column(name = "CURRENCY_CODE")
    private String currencyCode;            // (e.g., BNB, BTC, USDT, ETH)

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "WALLET_TYPE")
    private String walletType;          // e.g.,  (e.g., BSC, BTC, ERC20, TRC20)  NETWORK

    @Column(name = "WALLET_ADDRESS")
    private String walletAddress;       // e.g., user's BEP20 address

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "FEE_WORK")
    private BigDecimal feeWork  ;

    @Column(name = "FINAL_AMOUNT")
    private BigDecimal finalAmount  ;

    @Column(name = "STATUS")
    private String status = "PENDING";  // PENDING, APPROVED, REJECTED

    @Column(name = "TXN_ID")
    private String txId;

    @Column(name = "UPDATED_AT_DATE_TIME")
    private LocalDateTime updatedAtDateTime;

    @Column(name = "TRANSACTION_PASSWORD")
    private String transactionPassword;

    @Column(name = "OTP")
    private String otp;

    public Integer getWithdrawalRequestPkId() {
        return withdrawalRequestPkId;
    }

    public void setWithdrawalRequestPkId(Integer withdrawalRequestPkId) {
        this.withdrawalRequestPkId = withdrawalRequestPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAtDateTime() {
        return updatedAtDateTime;
    }

    public void setUpdatedAtDateTime(LocalDateTime updatedAtDateTime) {
        this.updatedAtDateTime = updatedAtDateTime;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getFeeWork() {
        return feeWork;
    }

    public void setFeeWork(BigDecimal feeWork) {
        this.feeWork = feeWork;
    }


    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
