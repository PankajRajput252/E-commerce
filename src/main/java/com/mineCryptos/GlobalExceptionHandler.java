package com.mineCryptos;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FinalException.class)
    public ResponseEntity<?> handleFinalException(FinalException ex) {

        FinalResponse response = new FinalResponse();
        Util.setMessage(response, "100", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
}
