package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "rank_rewards")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class IndividualRankReward  extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDIVIDUAL_RANK_PK_ID")
    private Integer individualRankPkId;

    @Column(name = "RANK_NAME")
    private String rankName;

    @Column(name = "RANK_CODE_FK_ID")
    private String rankCodeFkId;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;

    @Column(name = "MATCHING")
    private int matching;

    @Column(name = "REWARD")
    private double reward;

    @Column(name = "ACHIEVED")
    private boolean achieved;

    public Integer getIndividualRankPkId() {
        return individualRankPkId;
    }

    public void setIndividualRankPkId(Integer individualRankPkId) {
        this.individualRankPkId = individualRankPkId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getRankCodeFkId() {
        return rankCodeFkId;
    }

    public void setRankCodeFkId(String rankCodeFkId) {
        this.rankCodeFkId = rankCodeFkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
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

    public boolean getAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
}
