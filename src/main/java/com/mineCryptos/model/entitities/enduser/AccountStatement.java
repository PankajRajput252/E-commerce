package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_statement")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class AccountStatement  extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_STATEMENT_PK_ID")
    private Integer accountStatementPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "LOCAL_DATE_TIME")
    private LocalDateTime localDateTime;

    @Column(name = "PARTICULAR")
     private String particular;

    @Column(name = "CREDIT")
     private double credit;

    @Column(name = "DEBIT")
    private double debit;

    public AccountStatement() {
    }

    public Integer getAccountStatementPkId() {
        return accountStatementPkId;
    }

    public void setAccountStatementPkId(Integer accountStatementPkId) {
        this.accountStatementPkId = accountStatementPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }
}
