package com.mineCryptos;

public class ExceptionResponse {
    private String status;
    private String statusCode;
    private String message;
    private long timeStamp;

    public ExceptionResponse(){

    }

    public ExceptionResponse(String status, String statusCode, long timeStamp) {
        this.status = status;
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
