package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_map")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class UserMap extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_MAP_PK_ID")
    private Integer userMapPkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "RANK")
    private String rank;    // e.g. rank: SILVER, GOLD etc.

    // For binary system
    @Column(name = "LEFT_VOLUME")
    private BigDecimal leftVolume = BigDecimal.ZERO;

    @Column(name = "RIGHT_VOLUME")
    private BigDecimal rightVolume = BigDecimal.ZERO;

    // investment for mining
    @Column(name = "MINING_INVESTMENT")
    private BigDecimal miningInvestment = BigDecimal.ZERO;

    // node share percentage (0-100)
    @Column(name = "NODE_SHARE_PERCENT")
    private BigDecimal nodeSharePercent = BigDecimal.ZERO;

    public Integer getUserMapPkId() {
        return userMapPkId;
    }

    public void setUserMapPkId(Integer userMapPkId) {
        this.userMapPkId = userMapPkId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public BigDecimal getLeftVolume() {
        return leftVolume;
    }

    public void setLeftVolume(BigDecimal leftVolume) {
        this.leftVolume = leftVolume;
    }

    public BigDecimal getRightVolume() {
        return rightVolume;
    }

    public void setRightVolume(BigDecimal rightVolume) {
        this.rightVolume = rightVolume;
    }

    public BigDecimal getMiningInvestment() {
        return miningInvestment;
    }

    public void setMiningInvestment(BigDecimal miningInvestment) {
        this.miningInvestment = miningInvestment;
    }

    public BigDecimal getNodeSharePercent() {
        return nodeSharePercent;
    }

    public void setNodeSharePercent(BigDecimal nodeSharePercent) {
        this.nodeSharePercent = nodeSharePercent;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }
}
