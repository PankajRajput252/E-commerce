package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.DepositFund;
import com.mineCryptos.model.entitities.enduser.WalletTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface WalletTransactionRepository  extends JpaRepository<WalletTransaction,Integer> {
    List<WalletTransaction> findByActiveStateCodeFkId(String active, Pageable pageable);

    WalletTransaction findByWalletTxnPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByWalletTxnPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);
}
