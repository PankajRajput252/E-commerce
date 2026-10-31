package com.gunwala.repository;

import com.gunwala.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailOtpRepository extends JpaRepository<EmailOtp,Integer> {
    EmailOtp findTopByUserNodeIdOrderByCreatedDatetimeDesc(String userNodeId);
}
