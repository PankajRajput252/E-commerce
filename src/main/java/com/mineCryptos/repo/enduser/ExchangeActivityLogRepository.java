package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.ExchangeActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeActivityLogRepository extends JpaRepository<ExchangeActivityLog,Integer> {
    ExchangeActivityLog findByExchangeActivityLogPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<ExchangeActivityLog> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByExchangeActivityLogPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkId(String filterBy);
}
