package com.gunwala.exception;

import lombok.Getter;

@Getter
public class DuplicateTransactionReferenceException extends ConflictException {

    public static final String CODE = "DUPLICATE_TRANSACTION_REFERENCE";

    private final String transactionReference;

    /** Id of the user's own earlier payment, or null if it belongs to someone else. */
    private final Long existingPaymentId;

    public DuplicateTransactionReferenceException(String message, String transactionReference, Long existingPaymentId) {
        super(message);
        this.transactionReference = transactionReference;
        this.existingPaymentId = existingPaymentId;
    }
}