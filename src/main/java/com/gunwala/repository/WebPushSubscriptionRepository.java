package com.gunwala.repository;

import com.gunwala.entity.WebPushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WebPushSubscriptionRepository extends JpaRepository<WebPushSubscription, Long> {

    Optional<WebPushSubscription> findByEndpoint(String endpoint);

    List<WebPushSubscription> findByUserId(String userId);

    @Modifying
    @Transactional
    void deleteByEndpoint(String endpoint);
}
