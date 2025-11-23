package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailOtpRepository extends JpaRepository<EmailOtp,Integer> {
    EmailOtp findTopByUserNodeIdOrderByCreatedDatetimeDesc(String userNodeId);
}
