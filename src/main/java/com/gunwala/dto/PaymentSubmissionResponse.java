package com.gunwala.dto;



import com.gunwala.entity.PaymentMethodType;
import com.gunwala.entity.PaymentStatus;
import com.gunwala.entity.PaymentSubmission;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PaymentSubmissionResponse(
        Long id,
        String userId,
        Long paymentMethodId,
        PaymentMethodType paymentMethodType,
        String paymentMethodName,
        BigDecimal amount,
        String transactionReference,
        LocalDate paymentDate,
        String remarks,
        String receiptUrl,
        String receiptFileName,
        PaymentStatus status,
        String rejectionReason,
        String reviewedBy,
        Instant reviewedAt,
        Instant submittedAt
) {
    /** Must be called inside a transaction (or with paymentMethod fetched). */
    public static PaymentSubmissionResponse from(PaymentSubmission s, boolean adminView) {
        String receiptUrl = (adminView ? "/api/admin/payments/" : "/api/payments/") + s.getId() + "/receipt";
        return new PaymentSubmissionResponse(
                s.getId(), s.getUserId(),
                s.getPaymentMethod().getId(),
                s.getPaymentMethod().getType(),
                s.getPaymentMethod().getDisplayName(),
                s.getAmount(), s.getTransactionReference(), s.getPaymentDate(), s.getUserRemarks(),
                receiptUrl, s.getReceiptOriginalName(),
                s.getStatus(), s.getRejectionReason(),
                adminView ? s.getReviewedBy() : null,   // don't expose admin usernames to end users
                s.getReviewedAt(), s.getCreatedAt());
    }
}
