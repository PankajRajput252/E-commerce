package com.mineCryptos.repo.admin;

import com.mineCryptos.model.entitities.admin.RankReward;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankRewardRepository extends JpaRepository<RankReward,Integer> {
    RankReward findByRankIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<RankReward> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByActiveStateCodeFkId(String active);

    int countByRankIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByRankNameAndActiveStateCodeFkId(String rankName, String active);
}
