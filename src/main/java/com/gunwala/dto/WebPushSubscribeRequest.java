package com.gunwala.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** The browser's PushSubscription.toJSON(), sent by the website after the user allows notifications. */
public record WebPushSubscribeRequest(
        @NotBlank @Size(max = 700) String endpoint,
        @NotNull @Valid Keys keys
) {
    public record Keys(
            @NotBlank @Size(max = 128) String p256dh,
            @NotBlank @Size(max = 64) String auth
    ) {}
}
