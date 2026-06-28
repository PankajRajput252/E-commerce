package com.gunwala.repo.gunwala;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.gunwala.WeaponCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WeaponCategoryRepo extends JpaRepository<WeaponCategory,Integer> {
    FinalResponse<WeaponCategory> findByWeaponCategoryPkId(int i);

    FinalResponse deleteByWeaponCategoryPkId(int i);

    @Modifying
    @Query(value = "UPDATE weapon_category SET WEAPON_CATEGORY_NAME=:weaponCategoryName WHERE WEAPON_CATEGORY_PK_ID=:weaponCategoryPkId",nativeQuery = true)
    void updateWeaponCategory(String weaponCategoryName, int weaponCategoryPkId);
}
