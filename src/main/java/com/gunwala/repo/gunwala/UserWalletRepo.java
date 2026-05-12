package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UserWalletRepo extends JpaRepository<UserWallet,Integer> {


    List<UserWallet> findByUserFkId(String userFkId);

    List<UserWallet> findByUserWalletPkId(Integer userWalletPkId);

    void deleteByUserFkId(String userFkId);

    @Modifying
    @Query(value = "UPDATE user_wallet SET USER_FK_ID=:userFkId,CURRENCY_CODE=:currecyCode,AMOUNT=:amount,PAID_FOR=:paidFor,CREATED_DATETIME=:createdDatetime WHERE USER_WALLET_PK_ID=:userWalletPkId",nativeQuery = true)
    void updateUserWallet(int userWalletPkId, String userFkId, String paidFor, BigDecimal amount, String currecyCode, LocalDateTime createdDatetime);

    int countByCurrecyCode(String currecyCode);
}
