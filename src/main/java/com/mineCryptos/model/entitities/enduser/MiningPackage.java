package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mining_package")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class MiningPackage extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MINING_PACKAGE_PK_ID")
    private Integer miningPackagePkId;

    @Column(name = "USER_NODE_CODE")
    private String userNodeCode;

    @Column(name = "PACKAGE_AMOUNT")
    private BigDecimal packageAmount;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "MODE")
    private String mode;

    @Column(name = "TRANSACTION_PASSWORD")
    private String transactionPassword;

    @Column(name = "PACKAGE_STATUS")
    private String packageStatus;   // IN_PROGRESS , APPROVED

    @Column(name = "LOCAL_DATE_TIME")
    private LocalDateTime localDateTime;

    @Transient
    private double nodeAmount;

    public Integer getMiningPackagePkId() {
        return miningPackagePkId;
    }

    public void setMiningPackagePkId(Integer miningPackagePkId) {
        this.miningPackagePkId = miningPackagePkId;
    }

    public String getUserNodeCode() {
        return userNodeCode;
    }

    public void setUserNodeCode(String userNodeCode) {
        this.userNodeCode = userNodeCode;
    }

    public BigDecimal getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(BigDecimal packageAmount) {
        this.packageAmount = packageAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public double getNodeAmount() {
        return nodeAmount;
    }

    public void setNodeAmount(double nodeAmount) {
        this.nodeAmount = nodeAmount;
    }
}
