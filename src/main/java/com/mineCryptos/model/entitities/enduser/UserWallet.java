package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "user_wallet")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class UserWallet extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_WALLET_PK_ID")
    private Integer userWalletPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "WALLET_ADDRESS")
    private String walletAddress;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "FRONZEN_BALANCE")
    private BigDecimal frozenBalance; // hold amount during withdrawal

    public Integer getUserWalletPkId() {
        return userWalletPkId;
    }

    public void setUserWalletPkId(Integer userWalletPkId) {
        this.userWalletPkId = userWalletPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }
}
