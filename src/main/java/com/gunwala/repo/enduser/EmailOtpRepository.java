package com.gunwala.repo.enduser;

import com.gunwala.model.entitities.enduser.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailOtpRepository extends JpaRepository<EmailOtp,Integer> {
    EmailOtp findTopByUserNodeIdOrderByCreatedDatetimeDesc(String userNodeId);
}
