package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.IndividualIncomeSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualIncomeSummaryRepository extends JpaRepository<IndividualIncomeSummary,Integer> {
    IndividualIncomeSummary findByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<IndividualIncomeSummary> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByActiveStateCodeFkId(String active);

    int countByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);
}
