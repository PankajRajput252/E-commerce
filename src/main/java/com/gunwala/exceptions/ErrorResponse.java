package com.gunwala.exceptions;

public class ErrorResponse {
    private int statusCode;
    private String msg;

    public ErrorResponse(){};

    public ErrorResponse(int statusCode,String msg){
        this.statusCode=statusCode;
        this.msg=msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
