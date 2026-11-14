package com.gunwala.consultation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationTopicRepository extends JpaRepository<ConsultationTopic, Long> {
    List<ConsultationTopic> findAllByOrderBySortOrderAscNameAsc();

    List<ConsultationTopic> findByActiveTrueOrderBySortOrderAscNameAsc();
}
