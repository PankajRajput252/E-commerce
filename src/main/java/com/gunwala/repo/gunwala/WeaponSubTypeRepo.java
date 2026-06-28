package com.gunwala.repo.gunwala;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.gunwala.WeaponSubType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeaponSubTypeRepo extends JpaRepository<WeaponSubType,Integer> {
    FinalResponse deleteByWeaponSubTypePkId(int i);

    List<WeaponSubType> findByweaponSubTypePkId(Integer weaponSubTypePkIdInt);

    List<WeaponSubType> findByWeaponTypeFkId(int i);
}
