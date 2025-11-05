package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.MiningPackage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MiningPackageRepository extends JpaRepository<MiningPackage,Integer>{
    List<MiningPackage> findByActiveStateCodeFkId(String active, Pageable pageable);

    MiningPackage findByMiningPackagePkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);

    int countByMiningPackagePkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<MiningPackage> findByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId);
}
