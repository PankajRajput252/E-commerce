package com.gunwala.dto;

import com.gunwala.entity.PushDevice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Sent by the mobile app after login and whenever Firebase gives it a new token. */
public record RegisterDeviceRequest(
        @NotBlank @Size(max = 512) String token,
        @NotNull PushDevice.Platform platform
) {}
