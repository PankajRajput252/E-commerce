package com.gunwala.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(FinalException.class)
    public ErrorResponse handledFinalException(FinalException ex){
        return new ErrorResponse(400, ex.getMessage());
    }
}
