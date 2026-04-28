package com.gunwala.repo.enduser;

import com.gunwala.model.entitities.enduser.SupportTicket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    SupportTicket findBySupportTicketPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<SupportTicket> findByActiveStateCodeFkId(String active, Pageable pageable);

    List<SupportTicket> findByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId, Pageable pageable);

    Long countByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId);

    Long countByActiveStateCodeFkId(String filterBy);

    Long countBySupportTicketPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    Long countByUserNodeIdAndActiveStateCodeFkId(String inputFkId, String active);

    List<SupportTicket> findByUserNodeIdAndActiveStateCodeFkId(String inputFkId, String active);
}
