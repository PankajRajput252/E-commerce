package com.gunwala.dto;

import com.gunwala.model.ProductId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RejectPaymentRequest(
        @NotBlank(message = "Rejection reason is required")
        @Size(min = 10, max = 500, message = "Rejection reason must be 10-500 characters")
        String reason,

        List<ProductId> productIds
) {}