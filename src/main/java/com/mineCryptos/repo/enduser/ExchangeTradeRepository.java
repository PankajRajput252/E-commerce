package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.ExchangeActivityLog;
import com.mineCryptos.model.entitities.enduser.ExchangeTrade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeTradeRepository extends JpaRepository<ExchangeTrade, Integer> {
    List<ExchangeTrade> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    ExchangeTrade findByExchangeTradePkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByExchangeTradePkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkId(String filterBy);

    ExchangeTrade findByRequestIdAndActiveStateCodeFkId(Integer requestId, String active);

    ExchangeTrade findByRequestIdAndActiveStateCodeFkIdOrderByCreatedDatetimeDesc(Integer exchangeRequestPkId, String active);
}
