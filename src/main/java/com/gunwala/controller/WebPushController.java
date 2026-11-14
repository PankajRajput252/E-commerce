package com.gunwala.controller;

import com.gunwala.dto.WebPushSubscribeRequest;
import com.gunwala.exception.BadRequestException;
import com.gunwala.model.User;
import com.gunwala.service.Service.WebPushService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Website ↔ browser push notifications.
 *
 *   GET    /api/push/web/public-key                  (public)  { enabled, publicKey }
 *   POST   /api/push/web/subscriptions               (login)   PushSubscription.toJSON()
 *   DELETE /api/push/web/subscriptions?endpoint=...  (login)   on logout
 */
@RestController
@RequestMapping("/api/push/web")
@RequiredArgsConstructor
public class WebPushController {

    private final WebPushService webPushService;

    @GetMapping("/public-key")
    public Map<String, Object> publicKey() {
        Map<String, Object> body = new HashMap<>();
        body.put("enabled", webPushService.isEnabled());
        body.put("publicKey", webPushService.getPublicKey());
        return body;
    }

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@Valid @RequestBody WebPushSubscribeRequest request, @AuthenticationPrincipal User user) {
        if (!webPushService.isEnabled()) throw new BadRequestException("Web push is not configured on the server");
        webPushService.subscribe(nodeIdOf(user), request.endpoint().trim(),
                request.keys().p256dh().trim(), request.keys().auth().trim());
    }

    @DeleteMapping("/subscriptions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@RequestParam String endpoint, @AuthenticationPrincipal User user) {
        webPushService.unsubscribe(nodeIdOf(user), endpoint.trim());
    }

    /** Chat addresses users by nodeId, so subscriptions are stored under it too. */
    private static String nodeIdOf(User user) {
        if (user == null || !StringUtils.hasText(user.getNodeId())) {
            throw new BadRequestException("Your account has no user id; please log in again");
        }
        return user.getNodeId();
    }
}
