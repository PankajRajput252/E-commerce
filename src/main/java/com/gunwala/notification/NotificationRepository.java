package com.gunwala.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdAndAudienceOrderByCreatedAtDesc(String recipientId, String audience, Pageable pageable);

    Page<Notification> findByRecipientIdAndAudienceAndReadFalseOrderByCreatedAtDesc(String recipientId, String audience, Pageable pageable);

    long countByRecipientIdAndAudienceAndReadFalse(String recipientId, String audience);

    Optional<Notification> findByIdAndRecipientId(Long id, String recipientId);

    @Modifying
    @Query("update Notification n set n.read = :read where n.recipientId = :recipientId and n.groupKey = :groupKey")
    int setReadForGroup(@Param("recipientId") String recipientId, @Param("groupKey") String groupKey, @Param("read") boolean read);

    @Modifying
    @Query("update Notification n set n.read = true where n.recipientId = :recipientId and n.audience = :audience and n.read = false")
    int markAllRead(@Param("recipientId") String recipientId, @Param("audience") String audience);

    @Modifying
    @Query("delete from Notification n where n.createdAt < :before")
    int deleteOlderThan(@Param("before") Instant before);

    /** Everyone who should receive store activity. */
    @Query("select distinct u.nodeId from User u join u.roles r where r.name = 'ADMIN_USER' and u.nodeId is not null and u.activeStateCodeFkId = 'ACTIVE'")
    List<String> findAdminNodeIds();
}
