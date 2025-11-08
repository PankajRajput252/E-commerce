package com.mineCryptos.model.entitities.admin;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "income_type")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class IncomeType extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INCOME_TYPE_PK_ID")
    private Integer incomeTypePkId;

    @Column(name = "INCOME_NAME")
    private String incomeName;

    @Column(name = "INCOME_TYPE_CODE")
    private String incomeTypeCode;

    @Column(name = "LEVEL")
    private Integer level;

    @Column(name = "RULE_MODE",  columnDefinition ="VARCHAR(200) default 'PERCENT'")
    private  String ruleMode;   // FIXED

    // Optional configuration fields
    @Column(name = "PERCENTAGE")
    private BigDecimal percentage;   // e.g., commission %

    public Integer getIncomeTypePkId() {
        return incomeTypePkId;
    }

    public void setIncomeTypePkId(Integer incomeTypePkId) {
        this.incomeTypePkId = incomeTypePkId;
    }

    public String getIncomeName() {
        return incomeName;
    }

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getIncomeTypeCode() {
        return incomeTypeCode;
    }

    public void setIncomeTypeCode(String incomeTypeCode) {
        this.incomeTypeCode = incomeTypeCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRuleMode() {
        return ruleMode;
    }

    public void setRuleMode(String ruleMode) {
        this.ruleMode = ruleMode;
    }
}
