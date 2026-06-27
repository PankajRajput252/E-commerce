package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.WeaponCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeaponCategoryRepository extends JpaRepository<WeaponCategory ,Integer> {
    WeaponCategory findByWeaponCategoryPkId(Integer weaponCategoryPkId);
}
