package com.mineCryptos.repo.admin;

import com.mineCryptos.model.entitities.admin.SubscriptionDefinition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionDefinitionRepo extends JpaRepository<SubscriptionDefinition, Integer> {
    SubscriptionDefinition findBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(String inputPkId, String active);

    List<SubscriptionDefinition> findByActiveStateCodeFkId(String active, Pageable pageable);

    List<SubscriptionDefinition> findByActiveStateCodeFkIdAndSubscriptionCode(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkId(String active);

    int countBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(String inputPkId, String active);

    int countByActiveStateCodeFkIdAndSubscriptionCode(String active, String inputFkId);
}
