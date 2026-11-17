package com.gunwala.notification;

import com.gunwala.dto.PageResponse;
import com.gunwala.exception.BadRequestException;
import com.gunwala.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The signed-in admin's notifications (bell in the admin header).
 *
 *   GET  /api/admin/notifications?unreadOnly=false&page=0&size=30
 *   GET  /api/admin/notifications/unread-count    → { count }
 *   POST /api/admin/notifications/{id}/read       (also /unread) — applies to the whole group
 *   POST /api/admin/notifications/read-all
 *
 * Live updates: STOMP /topic/notifications/{nodeId}
 */
@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasAuthority('ADMIN_USER')")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public PageResponse<NotificationResponse> list(@AuthenticationPrincipal User user,
                                                   @RequestParam(defaultValue = "false") boolean unreadOnly,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "30") int size) {
        return PageResponse.of(service.list(nodeId(user), unreadOnly, page, size));
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal User user) {
        return Map.of("count", service.unreadCount(nodeId(user)));
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void read(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.setRead(nodeId(user), id, true);
    }

    @PostMapping("/{id}/unread")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unread(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.setRead(nodeId(user), id, false);
    }

    @PostMapping("/read-all")
    public Map<String, Integer> readAll(@AuthenticationPrincipal User user) {
        return Map.of("updated", service.markAllRead(nodeId(user)));
    }

    private static String nodeId(User user) {
        if (user == null || !StringUtils.hasText(user.getNodeId())) {
            throw new BadRequestException("Please sign in again.");
        }
        return user.getNodeId();
    }
}
