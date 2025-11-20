package com.mineCryptos;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class FinalException extends RuntimeException {

    public FinalException() {
        super();
    }

    public FinalException(String message) {
        super(message);
    }

    public FinalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinalException(Throwable cause) {
        super(cause);
    }

    public FinalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
