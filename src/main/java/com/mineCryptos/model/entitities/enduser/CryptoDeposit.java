package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "crypto_deposit")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class CryptoDeposit extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CRYPTOP_DEPOSIT_PK_ID")
    private Integer cryptotopDepositPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "PAY_ADDRESS")
    private String payAddress;

    @Column(name = "PAY_IN_ADDRESS")
    private String payInaddress;

    @Column(name = "PAY_OUT_ADDRESS")
    private String payoutAddress;

    @Column(name = "PAYMENT_ID")
    private String paymentId;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "TX_HASH")
    private String txHash;

    @Transient
    private Integer depositPkId;

    public Integer getCryptotopDepositPkId() {
        return cryptotopDepositPkId;
    }

    public void setCryptotopDepositPkId(Integer cryptotopDepositPkId) {
        this.cryptotopDepositPkId = cryptotopDepositPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayAddress() {
        return payAddress;
    }

    public void setPayAddress(String payAddress) {
        this.payAddress = payAddress;
    }

    public String getPayInaddress() {
        return payInaddress;
    }

    public void setPayInaddress(String payInaddress) {
        this.payInaddress = payInaddress;
    }

    public String getPayoutAddress() {
        return payoutAddress;
    }

    public void setPayoutAddress(String payoutAddress) {
        this.payoutAddress = payoutAddress;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getDepositPkId() {
        return depositPkId;
    }

    public void setDepositPkId(Integer depositPkId) {
        this.depositPkId = depositPkId;
    }
}
