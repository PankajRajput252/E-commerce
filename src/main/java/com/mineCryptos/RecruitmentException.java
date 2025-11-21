package com.mineCryptos;

public class RecruitmentException extends RuntimeException {

    public RecruitmentException() {

    }

    public RecruitmentException(String message) {
        super(message);
    }

    public RecruitmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecruitmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
