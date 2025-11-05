package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.DepositFund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositFundRepository extends JpaRepository<DepositFund,Integer> {
    DepositFund findByDepositPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<DepositFund> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByDepositPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);

    List<DepositFund> findByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId);
}
