package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Wallet findByWalletPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<Wallet> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByWalletPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);


    @Query("SELECT t.capitalWallet FROM Wallet t where t.userNodeCode = ?1 And t.activeStateCodeFkId =?2")
    double fetchUserCapitalWalletAmount(String userNodeCode, String active);


    @Query("SELECT t.nodeWallet FROM Wallet t where t.userNodeCode = ?1 And t.activeStateCodeFkId =?2")
    double fetchUserNodeWalletAmount(String userNodeCode, String active);


    @Query("SELECT t.mineWallet FROM Wallet t where t.userNodeCode = ?1 And t.activeStateCodeFkId =?2")
    double fetchUserMineWalletAmount(String userNodeCode, String active);

    @Modifying
    @Query(value = "UPDATE eu_wallet\n" +
            "SET CAPITAL_WALLET = ?1 \n" +
            "WHERE USER_NODE_CODE = ?2 \n" +
            "  AND ACTIVE_STATE_CODE_FK_ID = 'ACTIVE' AND SAVE_STATE_CODE_FK_ID = 'SAVED' AND RECORD_STATE_CODE_FK_ID = 'CURRENT' AND IS_DELETED = 0  ", nativeQuery = true)
    public void updateCapitalWalletOfUser(double capitalAmount, String userFkId);


    @Modifying
    @Query(value = "UPDATE eu_wallet\n" +
            "SET NODE_WALLET = ?1 \n" +
            "WHERE USER_NODE_CODE = ?2 \n" +
            "  AND ACTIVE_STATE_CODE_FK_ID = 'ACTIVE' AND SAVE_STATE_CODE_FK_ID = 'SAVED' AND RECORD_STATE_CODE_FK_ID = 'CURRENT' AND IS_DELETED = 0  ", nativeQuery = true)
    public void updateNodeWalletOfUser(double capitalAmount, String userFkId);


    @Modifying
    @Query(value = "UPDATE eu_wallet\n" +
            "SET MINE_WALLET = ?1 \n" +
            "WHERE USER_NODE_CODE = ?2 \n" +
            "  AND ACTIVE_STATE_CODE_FK_ID = 'ACTIVE' AND SAVE_STATE_CODE_FK_ID = 'SAVED' AND RECORD_STATE_CODE_FK_ID = 'CURRENT' AND IS_DELETED = 0  ", nativeQuery = true)
    public void updateMineWalletOfUser(double capitalAmount, String userFkId);


    List<Wallet> findByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId);

    @Query("SELECT SUM(w.mineWallet) FROM Wallet w")
    Double getTotalMineWallet();

    @Query("SELECT SUM(w.nodeWallet) FROM Wallet w")
    Double getTotalNodeWallet();

    @Query("SELECT SUM(w.capitalWallet) FROM Wallet w")
    Double getTotalCapitalWallet();
}
