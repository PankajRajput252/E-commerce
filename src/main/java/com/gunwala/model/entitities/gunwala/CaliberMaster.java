package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "caliber_master")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class CaliberMaster extends StandardFieldClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CALIBER_PK_ID")
    private  int caliberPkId;

    @Column(name = "WEAPON_TYPE_FK_ID")
    private  int weaponTypeFkId;

    @Column(name = "CALIBER_NAME")
    private  String caliberName;

    public int getCaliberPkId() {
        return caliberPkId;
    }

    public void setCaliberPkId(int caliberPkId) {
        this.caliberPkId = caliberPkId;
    }

    public int getWeaponTypeFkId() {
        return weaponTypeFkId;
    }

    public void setWeaponTypeFkId(int weaponTypeFkId) {
        this.weaponTypeFkId = weaponTypeFkId;
    }

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }
}
