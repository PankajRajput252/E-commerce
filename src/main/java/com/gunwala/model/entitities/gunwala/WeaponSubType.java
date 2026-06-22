package com.gunwala.model.entitities.gunwala;


import com.gunwala.model.StandardFieldClass;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "weapon_sub_type")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class WeaponSubType  extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WEAPON_SUB_TYPE_PK_ID")
    private  int weaponSubTypePkId;

    @Column(name = "WEAPON_TYPE_Fk_ID")
    private  int weaponTypeFkId;

    @Column(name = "WEAPON_SUB_TYPE_NAME")
    private  String weaponSubTypeName;

    public int getWeaponSubTypePkId() {
        return weaponSubTypePkId;
    }

    public void setWeaponSubTypePkId(int weaponSubTypePkId) {
        this.weaponSubTypePkId = weaponSubTypePkId;
    }

    public int getWeaponTypeFkId() {
        return weaponTypeFkId;
    }

    public void setWeaponTypeFkId(int weaponTypeFkId) {
        this.weaponTypeFkId = weaponTypeFkId;
    }

    public String getWeaponSubTypeName() {
        return weaponSubTypeName;
    }

    public void setWeaponSubTypeName(String weaponSubTypeName) {
        this.weaponSubTypeName = weaponSubTypeName;
    }
}
