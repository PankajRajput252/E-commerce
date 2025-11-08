package com.mineCryptos.repo;

import com.mineCryptos.model.entitities.enduser.CommissionLedger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommissionLedgerRepository extends JpaRepository<CommissionLedger, Integer> {
    CommissionLedger findByCommissionLedgerPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<CommissionLedger> findByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId, Pageable pageable);

    List<CommissionLedger> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByCommissionLedgerPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);

    int countByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId);
}
