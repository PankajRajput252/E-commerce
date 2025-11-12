package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.AccountStatement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountStatementRepository extends JpaRepository<AccountStatement, Integer> {
    AccountStatement findByAccountStatementPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<AccountStatement> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    List<AccountStatement> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByAccountStatementPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);
}
