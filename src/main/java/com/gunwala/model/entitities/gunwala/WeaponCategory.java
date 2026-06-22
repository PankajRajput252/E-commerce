package com.gunwala.model.entitities.gunwala;


import com.gunwala.model.StandardFieldClass;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "weapon_category")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class WeaponCategory extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WEAPON_CATEGORY_PK_ID")
    private  int weaponCategoryPkId;

    @Column(name = "WEAPON_CATEGORY_NAME")
    private  String weaponCategoryName;

    public int getWeaponCategoryPkId() {
        return weaponCategoryPkId;
    }

    public void setWeaponCategoryPkId(int weaponCategoryPkId) {
        this.weaponCategoryPkId = weaponCategoryPkId;
    }

    public String getWeaponCategoryName() {
        return weaponCategoryName;
    }

    public void setWeaponCategoryName(String weaponCategoryName) {
        this.weaponCategoryName = weaponCategoryName;
    }
}
