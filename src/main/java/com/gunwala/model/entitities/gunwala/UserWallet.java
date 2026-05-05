package com.gunwala.model.entitities.gunwala;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_wallet")
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_WALLET_PK_ID")
    private int userWalletPkId;

    @Column(name = "USER_FK_ID")
    private String userFkId;

    @Column(name = "CURRENCY_CODE")
    private String currecyCode;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PAID_FOR")
    private String paidFor;

    @Column(name = "CREATED_DATETIME")
    private LocalDateTime CreatedDatetime;

    public int getUserWalletPkId() {
        return userWalletPkId;
    }

    public void setUserWalletPkId(int userWalletPkId) {
        this.userWalletPkId = userWalletPkId;
    }

    public String getUserFkId() {
        return userFkId;
    }

    public void setUserFkId(String userFkId) {
        this.userFkId = userFkId;
    }

    public String getCurrecyCode() {
        return currecyCode;
    }

    public void setCurrecyCode(String currecyCode) {
        this.currecyCode = currecyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaidFor() {
        return paidFor;
    }

    public void setPaidFor(String paidFor) {
        this.paidFor = paidFor;
    }

    public LocalDateTime getCreatedDatetime() {
        return CreatedDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        CreatedDatetime = createdDatetime;
    }
}
