package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.SupportTicket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    SupportTicket findBySupportTicketPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<SupportTicket> findByActiveStateCodeFkId(String active, Pageable pageable);

    List<SupportTicket> findByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeId(String active, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);

    int countBySupportTicketPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);
}
