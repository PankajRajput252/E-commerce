package com.mineCryptos.model.entitities.admin;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

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

    // Optional configuration fields
    @Column(name = "PERCENTAGE")
    private double percentage;   // e.g., commission %

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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
