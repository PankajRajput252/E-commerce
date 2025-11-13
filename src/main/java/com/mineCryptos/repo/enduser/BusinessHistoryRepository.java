package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.BusinessHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessHistoryRepository extends JpaRepository<BusinessHistory,Integer> {
    BusinessHistory findByBusinessHistoryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<BusinessHistory> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    List<BusinessHistory> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    int countByBusinessHistoryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);
}
