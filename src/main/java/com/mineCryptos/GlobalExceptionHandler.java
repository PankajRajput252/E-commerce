package com.mineCryptos;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(FinalException.class)
//    public ResponseEntity<?> handleFinalException(FinalException ex) {
//
//        FinalResponse response = new FinalResponse();
//        Util.setMessage(response, "100", ex.getMessage());
//
//        return ResponseEntity.badRequest().body(response);
//    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(FinalException exc){
        ExceptionResponse error = new ExceptionResponse();
//        error.setStatus(HttpStatus.BAD_REQUEST.value()); to make tapresponse and advice uniform
        error.setStatusCode("100");
        error.setStatus(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(RecruitmentException exc){
        ExceptionResponse error = new ExceptionResponse();
        error.setStatusCode("100");
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
