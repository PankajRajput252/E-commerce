package com.mineCryptos;

public class FinalException extends RuntimeException {
    public FinalException() {
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
