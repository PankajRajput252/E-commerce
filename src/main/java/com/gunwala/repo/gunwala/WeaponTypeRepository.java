package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.WeaponType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeaponTypeRepository extends JpaRepository<WeaponType,Integer> {

    List<WeaponType> findByWeaponCategoryFkId(Integer weaponCategoryFkId);

    List<WeaponType> findByWeaponCategoryFkId(Integer weaponCategoryFkId,
                                              Pageable pageable);

    long countByWeaponCategoryFkId(Integer weaponCategoryFkId);

    WeaponType findByWeaponTypePkId(Integer weaponTypePkId);
}