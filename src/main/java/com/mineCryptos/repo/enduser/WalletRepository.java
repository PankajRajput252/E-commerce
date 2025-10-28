package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Wallet findByWalletPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<Wallet> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByWalletPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);
}
