package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "income_summary")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class IncomeSummary extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INCOME_SUMMARY_PK_ID")
    private Integer incomeSummaryPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;   // direct_income ,  level_income,  total_income

    @Column(name = "BONUS_AMOUNT")
    private Double bonusAmount;

    public Integer getIncomeSummaryPkId() {
        return incomeSummaryPkId;
    }

    public void setIncomeSummaryPkId(Integer incomeSummaryPkId) {
        this.incomeSummaryPkId = incomeSummaryPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Double bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
}
