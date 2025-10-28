package com.mineCryptos.model.entitities.admin;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "rank_rewards")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class RankReward extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RANK_ID")
    private Integer rankId;

    @Column(name = "RANK_NAME")
    private String rankName;

    @Column(name = "MATCHING")
    private int matching;

    @Column(name = "REWARD")
    private double reward;

    @Column(name = "ACHIEVED")
    private boolean achieved;

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public int getMatching() {
        return matching;
    }

    public void setMatching(int matching) {
        this.matching = matching;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
}
