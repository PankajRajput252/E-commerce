package com.gunwala.notification;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String audience,
        String category,
        String actorName,
        String action,
        String title,
        String reference,
        String status,
        String link,
        String groupKey,
        boolean read,
        Instant createdAt
) {
    static NotificationResponse of(Notification n) {
        return new NotificationResponse(n.getId(), n.getAudience(), n.getCategory(), n.getActorName(), n.getAction(),
                n.getTitle(), n.getReference(), n.getStatus(), n.getLink(), n.getGroupKey(), n.isRead(), n.getCreatedAt());
    }
}
