package com.mineCryptos.model.entitities.admin;

import javax.persistence.*;

@Entity
@Table(name = "rank_master")
public class RankMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RANK_MASTER_PK_ID")
    private  Integer rankMasterPkId;

    @Column(name = "RANK_CODE")
    private  String rankCode;

    @Column(name = "RANK_NAME")
    private  String rankName;

    @Column(name = "RANK_LEVEL")
    private  String rankLevel;

    @Column(name = "STATUS")
    private  String status;

    public Integer getRankMasterPkId() {
        return rankMasterPkId;
    }

    public void setRankMasterPkId(Integer rankMasterPkId) {
        this.rankMasterPkId = rankMasterPkId;
    }

    public String getRankCode() {
        return rankCode;
    }

    public void setRankCode(String rankCode) {
        this.rankCode = rankCode;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
