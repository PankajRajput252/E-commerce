package com.gunwala.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.gunwala.entity.PaymentMethod;
import com.gunwala.entity.PaymentMethodType;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentMethodResponse(
        Long id,
        PaymentMethodType type,
        String displayName,
        String accountHolderName,
        String bankName,
        String accountNumber,
        String ifscCode,
        String branchName,
        String upiId,
        String qrImageUrl,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static PaymentMethodResponse from(PaymentMethod m) {
        String qrUrl = m.getType() == PaymentMethodType.QR_CODE
                ? "/api/payment-methods/" + m.getId() + "/qr-image"
                : null;
        return new PaymentMethodResponse(
                m.getId(), m.getType(), m.getDisplayName(),
                m.getAccountHolderName(), m.getBankName(), m.getAccountNumber(),
                m.getIfscCode(), m.getBranchName(), m.getUpiId(), qrUrl,
                m.isActive(), m.getCreatedAt(), m.getUpdatedAt());
    }
}
