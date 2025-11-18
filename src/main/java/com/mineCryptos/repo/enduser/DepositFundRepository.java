package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.DepositFund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepositFundRepository extends JpaRepository<DepositFund,Integer> {
    DepositFund findByDepositPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    List<DepositFund> findByActiveStateCodeFkId(String active, Pageable pageable);

    int countByDepositPkIdAndActiveStateCodeFkId(Integer inputPkId, String active);

    int countByActiveStateCodeFkId(String active);

    List<DepositFund> findByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId, Pageable pageable);

    int countByActiveStateCodeFkIdAndUserNodeCode(String active, String inputFkId);

    @Modifying
    @Query(value = "UPDATE deposit_fund\n" +
            "SET PAYMENT_ID = ?1 \n" +
            "WHERE USER_NODE_CODE = ?2  AND DEPOSIT_PK_ID=?3 \n" +
            "  AND ACTIVE_STATE_CODE_FK_ID = 'ACTIVE' AND SAVE_STATE_CODE_FK_ID = 'SAVED' AND RECORD_STATE_CODE_FK_ID = 'CURRENT' AND IS_DELETED = 0  ", nativeQuery = true)
    public void updatePaymentIdBasedOnPkId(String paymentId, String userNodeCode, Integer depositPkId);

}
