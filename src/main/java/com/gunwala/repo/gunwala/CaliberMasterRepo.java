package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.CaliberMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CaliberMasterRepo extends JpaRepository<CaliberMaster,Integer> {
    List<CaliberMaster> findByCaliberPkId(Integer caliberPkIdInt);

    List<CaliberMaster> findByWeaponTypeFkId(int i);

    void deleteByWeaponTypeFkId(int i);


    @Modifying
    @Query(value = "UPDATE caliber_master SET WEAPON_TYPE_FK_ID=:weaponTypeFkId,CALIBER_NAME=:caliberName WHERE CALIBER_PK_ID=:caliberPkId",nativeQuery = true)
    void updateCaliberMaster(int caliberPkId, int weaponTypeFkId, String caliberName);
}
