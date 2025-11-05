package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.WithdrawalRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Integer> {
    WithdrawalRequest findByWithdrawalRequestPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<WithdrawalRequest> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByWithdrawalRequestPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);

    List<WithdrawalRequest> findByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId);
}
