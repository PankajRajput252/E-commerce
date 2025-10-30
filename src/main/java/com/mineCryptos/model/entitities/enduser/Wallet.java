package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "eu_wallet")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class Wallet extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WALLET_PK_ID")
    private Integer walletPkId;

    @Column(name = "MINE_WALLET")
    private double mineWallet;

    @Column(name = "NODE_WALLET")
    private double nodeWallet;

    @Column(name = "CAPITAL_WALLET")
    private double capitalWallet;

    @Column(name = "TOTAL_CREDIT")
    private double totalCredit;

    @Column(name = "TOTAL_DEBIT")
    private double totalDebit;

    @Column(name = "USER_NODE_CODE")
    private String userNodeCode;



    public Wallet() {
        this.mineWallet = 0.0;
        this.nodeWallet = 0.0;
        this.capitalWallet = 0.0;
        this.totalCredit = 0.0;
        this.totalDebit = 0.0;
    }

    public Integer getWalletPkId() {
        return walletPkId;
    }

    public void setWalletPkId(Integer walletPkId) {
        this.walletPkId = walletPkId;
    }

    public double getMineWallet() {
        return mineWallet;
    }

    public void setMineWallet(double mineWallet) {
        this.mineWallet = mineWallet;
    }

    public double getNodeWallet() {
        return nodeWallet;
    }

    public void setNodeWallet(double nodeWallet) {
        this.nodeWallet = nodeWallet;
    }

    public double getCapitalWallet() {
        return capitalWallet;
    }

    public void setCapitalWallet(double capitalWallet) {
        this.capitalWallet = capitalWallet;
    }

    public double getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(double totalCredit) {
        this.totalCredit = totalCredit;
    }

    public double getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(double totalDebit) {
        this.totalDebit = totalDebit;
    }

    public String getUserNodeCode() {
        return userNodeCode;
    }

    public void setUserNodeCode(String userNodeCode) {
        this.userNodeCode = userNodeCode;
    }
}
