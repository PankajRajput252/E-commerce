package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.CryptoDeposit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CryptoDepositRepository extends JpaRepository<CryptoDeposit, Integer> {
    List<CryptoDeposit> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    List<CryptoDeposit> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);
}
