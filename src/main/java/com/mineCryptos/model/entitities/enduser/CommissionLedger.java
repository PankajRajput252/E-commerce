package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "commission_ledger")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class CommissionLedger extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMISSION_LEDGER_PK_ID")
    private  Integer commissionLedgerPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "INCOME_TYPE")
    private String incomeType;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "NOTE")
    private String note;


    @Column(name = "IS_SETTLED")
    private boolean isSettled;

    public Integer getCommissionLedgerPkId() {
        return commissionLedgerPkId;
    }

    public void setCommissionLedgerPkId(Integer commissionLedgerPkId) {
        this.commissionLedgerPkId = commissionLedgerPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean getIsSettled() {
        return isSettled;
    }

    public void setIsSettled(boolean settled) {
        isSettled = settled;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
