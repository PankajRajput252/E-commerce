package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.IncomeSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeSummaryRepository extends JpaRepository<IncomeSummary, Integer> {
    IncomeSummary findByIncomeSummaryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<IncomeSummary> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    List<IncomeSummary> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByIncomeSummaryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);
}
