package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.ExchangeRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Integer> {
    ExchangeRequest findByExchangeRequestPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<ExchangeRequest> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    List<ExchangeRequest> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByActiveStateCodeFkId(String filterBy);

    int countByExchangeRequestPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    List<ExchangeRequest> findByActiveStateCodeFkIdAndStatus(String filterBy, String open, Pageable pageable);

    int countByActiveStateCodeFkIdAndStatus(String filterBy, String open);
}
