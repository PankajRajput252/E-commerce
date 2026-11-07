package com.gunwala.exception;

import lombok.Getter;

/** 400 tied to one request field, returned as {"errors": {field: message}} so the form can show it in place. */
@Getter
public class FieldValidationException extends BadRequestException {

    private final String field;

    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
}
