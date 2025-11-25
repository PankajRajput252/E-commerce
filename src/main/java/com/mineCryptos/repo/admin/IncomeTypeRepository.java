package com.mineCryptos.repo.admin;

import com.mineCryptos.model.entitities.IncomeTypeEnum;
import com.mineCryptos.model.entitities.admin.IncomeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeTypeRepository extends JpaRepository<IncomeType,Integer> {
    IncomeType findByIncomeTypePkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<IncomeType> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByActiveStateCodeFkId(String active);

    int countByIncomeTypePkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    IncomeType findByActiveStateCodeFkIdAndIncomeTypeCode(String active, String code);

    List<IncomeType> findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum incomeTypeEnum, String active);
}
