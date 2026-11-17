package com.gunwala.notification;

import com.gunwala.model.User;
import com.gunwala.repo.UserRepository;
import com.gunwala.service.Service.WebPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Admin notifications (bell in the admin header): new orders, payments, bookings, sign-ups, listings…
 * Any service can call {@link #send}; one row is saved per admin and pushed live (STOMP
 * /topic/notifications/{nodeId}, plus browser push when no tab is open) after the caller's transaction
 * commits. A failure here is logged and never breaks the action that triggered it.
 */
@Slf4j
@Service
public class NotificationService {

    private static final Duration KEEP_FOR = Duration.ofDays(90);

    private final NotificationRepository repository;
    private final UserRepository users;
    private final SimpMessagingTemplate messaging;
    private final WebPushService webPush;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate txNew;
    private final TransactionTemplate tx;

    public NotificationService(NotificationRepository repository, UserRepository users, SimpMessagingTemplate messaging,
                               WebPushService webPush, ApplicationEventPublisher events, PlatformTransactionManager txManager) {
        this.repository = repository;
        this.users = users;
        this.messaging = messaging;
        this.webPush = webPush;
        this.events = events;
        this.tx = new TransactionTemplate(txManager);
        this.txNew = new TransactionTemplate(txManager);
        this.txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    // ============================================================ sending

    /** Queue a notification; delivered after the current transaction commits (or right away if none). */
    public void send(NotificationEvent event) {
        try {
            events.publishEvent(event);
        } catch (Exception e) {
            log.warn("Could not queue notification \"{}\": {}", event.action, e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void deliver(NotificationEvent e) {
        try {
            if (!StringUtils.hasText(e.action) || !StringUtils.hasText(e.title)) {
                log.warn("Notification without action/title ignored");
                return;
            }
            Optional<User> actor = findUser(e.actorId);
            String actorNodeId = actor.map(User::getNodeId).orElse(null);
            String actorName = StringUtils.hasText(e.actorName) ? e.actorName
                    : actor.map(User::getName).filter(StringUtils::hasText).orElse(null);

            List<String> recipients = repository.findAdminNodeIds();
            String audience = Notification.AUDIENCE_ADMIN;

            List<Notification> saved = new ArrayList<>();
            txNew.executeWithoutResult(status -> {
                for (String recipientId : new LinkedHashSet<>(recipients)) {
                    if (recipientId.equals(actorNodeId)) continue; // nobody is notified about their own action
                    Notification n = new Notification();
                    n.setRecipientId(recipientId);
                    n.setAudience(audience);
                    n.setCategory(e.category);
                    n.setActorId(actorNodeId);
                    n.setActorName(cut(actorName, 150));
                    n.setAction(cut(e.action, 200));
                    n.setTitle(cut(e.title, 300));
                    n.setReference(cut(e.reference, 80));
                    n.setStatus(cut(e.status, 40));
                    n.setLink(cut(e.link, 300));
                    n.setGroupKey(cut(e.groupKey, 100));
                    saved.add(repository.save(n));
                }
            });

            for (Notification n : saved) {
                NotificationResponse body = NotificationResponse.of(n);
                try {
                    messaging.convertAndSend("/topic/notifications/" + n.getRecipientId(), body);
                } catch (Exception ex) {
                    log.warn("Live notification to {} failed: {}", n.getRecipientId(), ex.getMessage());
                }
                webPush.notifyGeneric(n.getRecipientId(),
                        (actorName != null ? actorName : "Bandookwale") + " " + n.getAction(),
                        n.getTitle() + (n.getReference() != null ? "\n" + n.getReference() : ""),
                        n.getGroupKey(), n.getLink() != null ? n.getLink() : "/notifications");
            }
        } catch (Exception ex) {
            log.error("Could not deliver notification \"{}\": {}", e.action, ex.getMessage(), ex);
        }
    }

    // ============================================================ reading (current admin)

    public Page<NotificationResponse> list(String nodeId, boolean unreadOnly, int page, int size) {
        PageRequest p = PageRequest.of(Math.max(0, page), Math.min(Math.max(size, 1), 50));
        String aud = Notification.AUDIENCE_ADMIN;
        Page<Notification> result = unreadOnly
                ? repository.findByRecipientIdAndAudienceAndReadFalseOrderByCreatedAtDesc(nodeId, aud, p)
                : repository.findByRecipientIdAndAudienceOrderByCreatedAtDesc(nodeId, aud, p);
        return result.map(NotificationResponse::of);
    }

    public long unreadCount(String nodeId) {
        return repository.countByRecipientIdAndAudienceAndReadFalse(nodeId, Notification.AUDIENCE_ADMIN);
    }

    /** Marks one notification (and the rest of its group) read or unread. */
    public void setRead(String nodeId, Long id, boolean read) {
        tx.executeWithoutResult(status -> {
            Notification n = repository.findByIdAndRecipientId(id, nodeId)
                    .orElseThrow(() -> new com.gunwala.exception.NotFoundException("Notification not found"));
            if (StringUtils.hasText(n.getGroupKey())) {
                repository.setReadForGroup(nodeId, n.getGroupKey(), read);
            } else {
                n.setRead(read);
            }
        });
    }

    public int markAllRead(String nodeId) {
        Integer n = tx.execute(status -> repository.markAllRead(nodeId, Notification.AUDIENCE_ADMIN));
        return n == null ? 0 : n;
    }

    /** Keeps the table small: drops notifications older than 90 days, nightly. */
    @Scheduled(cron = "0 30 3 * * *")
    public void purgeOld() {
        try {
            Integer n = tx.execute(status -> repository.deleteOlderThan(Instant.now().minus(KEEP_FOR)));
            if (n != null && n > 0) log.info("Deleted {} notifications older than {} days", n, KEEP_FOR.toDays());
        } catch (Exception e) {
            log.warn("Notification cleanup failed: {}", e.getMessage());
        }
    }

    // ============================================================ helpers

    private Optional<User> findUser(String idOrEmail) {
        if (!StringUtils.hasText(idOrEmail)) return Optional.empty();
        try {
            if (idOrEmail.contains("@")) return users.findByEmail(idOrEmail.trim());
            return Optional.ofNullable(users.findByNodeIdAndActiveStateCodeFkId(idOrEmail, "ACTIVE"));
        } catch (Exception e) {
            // e.g. several rows for one email: fall back to "unknown actor" rather than failing
            log.debug("Could not resolve user {}: {}", idOrEmail, e.getMessage());
            return Optional.empty();
        }
    }

    private static String cut(String s, int max) {
        if (s == null) return null;
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
