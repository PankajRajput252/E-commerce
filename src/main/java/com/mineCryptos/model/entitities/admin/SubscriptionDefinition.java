package com.mineCryptos.model.entitities.admin;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adm_subscription_definition")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class SubscriptionDefinition extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBSCRIPTION_DEF_PK_ID")
    private Integer subscriptionDefinitionPkId;

    @Column(name = "SUBSCRIPTION_NAME")
    private String subscriptionName;

    @Column(name = "SUBSCRIPTION_CODE")
    private String subscriptionCode;

    @Column(name = "SUBSCRIPTION_AMOUNT")
    private double subscriptionAmount;

    @Column(name = "SUBSCRIPTION_START_DATETIME")
    private LocalDateTime subscriptionStartDateTime;

    @Column(name = "SUBSCRIPTION_ENDTIME")
    private LocalDateTime subscriptionEndDateTime;

    public Integer getSubscriptionDefinitionPkId() {
        return subscriptionDefinitionPkId;
    }

    public void setSubscriptionDefinitionPkId(Integer subscriptionDefinitionPkId) {
        this.subscriptionDefinitionPkId = subscriptionDefinitionPkId;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public double getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(double subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public LocalDateTime getSubscriptionStartDateTime() {
        return subscriptionStartDateTime;
    }

    public void setSubscriptionStartDateTime(LocalDateTime subscriptionStartDateTime) {
        this.subscriptionStartDateTime = subscriptionStartDateTime;
    }

    public LocalDateTime getSubscriptionEndDateTime() {
        return subscriptionEndDateTime;
    }

    public void setSubscriptionEndDateTime(LocalDateTime subscriptionEndDateTime) {
        this.subscriptionEndDateTime = subscriptionEndDateTime;
    }
}
