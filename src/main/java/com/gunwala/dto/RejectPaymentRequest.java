package com.gunwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectPaymentRequest(
        @NotBlank(message = "Rejection reason is required")
        @Size(min = 10, max = 500, message = "Rejection reason must be 10-500 characters")
        String reason
) {}
