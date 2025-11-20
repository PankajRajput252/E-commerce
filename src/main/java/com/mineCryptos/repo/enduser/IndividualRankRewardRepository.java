package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.IndividualRankReward;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualRankRewardRepository extends JpaRepository<IndividualRankReward, Integer> {
    IndividualRankReward findByIndividualRankPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<IndividualRankReward> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);

    List<IndividualRankReward> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByIndividualRankPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);

    IndividualRankReward findByUserNodeIdAndActiveStateCodeFkId(String nodeId, String active);
}
