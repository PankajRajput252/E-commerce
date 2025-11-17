package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWalletRepository extends JpaRepository<UserWallet, Integer> {
    UserWallet findByActiveStateCodeFkIdAndUserNodeId(String userNodeId, String active);
}
