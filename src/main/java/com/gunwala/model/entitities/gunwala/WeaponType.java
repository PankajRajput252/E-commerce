package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "weapon_type")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class WeaponType  extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WEAPON_TYPE_PK_ID")
    private  int weaponTypePkId;

    @Column(name = "WEAPON_CATEGORY_Fk_ID")
    private  int weaponCategoryFkId;

    @Column(name = "WEAPON_TYPE_NAME")
    private  String weaponTypeName;

    public int getWeaponTypePkId() {
        return weaponTypePkId;
    }

    public void setWeaponTypePkId(int weaponTypePkId) {
        this.weaponTypePkId = weaponTypePkId;
    }

    public int getWeaponCategoryFkId() {
        return weaponCategoryFkId;
    }

    public void setWeaponCategoryFkId(int weaponCategoryFkId) {
        this.weaponCategoryFkId = weaponCategoryFkId;
    }

    public String getWeaponTypeName() {
        return weaponTypeName;
    }

    public void setWeaponTypeName(String weaponTypeName) {
        this.weaponTypeName = weaponTypeName;
    }
}
