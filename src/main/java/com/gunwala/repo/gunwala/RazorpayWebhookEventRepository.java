package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.RazorpayWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RazorpayWebhookEventRepository extends JpaRepository<RazorpayWebhookEvent, Integer> {

    Optional<RazorpayWebhookEvent> findByRazorpayEventId(String razorpayEventId);

    boolean existsByRazorpayEventId(String razorpayEventId);

    List<RazorpayWebhookEvent> findByStatus(String status);

    List<RazorpayWebhookEvent> findByEventType(String eventType);
}
