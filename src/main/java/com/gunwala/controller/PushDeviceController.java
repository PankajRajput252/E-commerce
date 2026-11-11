package com.gunwala.controller;

import com.gunwala.dto.RegisterDeviceRequest;
import com.gunwala.exception.BadRequestException;
import com.gunwala.model.User;
import com.gunwala.service.Service.PushNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile app ↔ push notifications (login required).
 *
 * After login, and whenever Firebase hands the app a new token:
 *   POST   /api/push/devices   { "token": "<FCM token>", "platform": "ANDROID" | "IOS" }
 * On logout (before dropping the JWT):
 *   DELETE /api/push/devices?token=<FCM token>
 */
@RestController
@RequestMapping("/api/push/devices")
@RequiredArgsConstructor
public class PushDeviceController {

    private final PushNotificationService pushService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@Valid @RequestBody RegisterDeviceRequest request, @AuthenticationPrincipal User user) {
        pushService.registerDevice(nodeIdOf(user), request.token().trim(), request.platform());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@RequestParam String token, @AuthenticationPrincipal User user) {
        nodeIdOf(user); // must be logged in
        pushService.unregisterDevice(token.trim());
    }

    /** Chat addresses users by nodeId, so devices are stored under it too. */
    private static String nodeIdOf(User user) {
        if (user == null || !StringUtils.hasText(user.getNodeId())) {
            throw new BadRequestException("Your account has no user id; please log in again");
        }
        return user.getNodeId();
    }
}
