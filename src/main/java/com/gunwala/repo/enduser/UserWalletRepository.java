package com.gunwala.repo.enduser;

import com.gunwala.model.entitities.enduser.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWalletRepository extends JpaRepository<UserWallet, Integer> {
    UserWallet findByActiveStateCodeFkIdAndUserNodeId(String userNodeId, String active);
}
