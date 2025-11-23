package com.mineCryptos.repo.enduser;

import com.mineCryptos.model.entitities.enduser.UserWalletAddress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWalletAddressRepository extends JpaRepository<UserWalletAddress,Integer> {
    List<UserWalletAddress> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    UserWalletAddress findByUserWalletAddressPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<UserWalletAddress> findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId, Pageable pageable);
    UserWalletAddress findByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByUserWalletAddressPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndUserNodeId(String filterBy, String inputFkId);

    int countByActiveStateCodeFkId(String filterBy);
}
