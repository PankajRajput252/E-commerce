package com.gunwala.authbridge.model;

public class AuthbridgeReportDetail {
    String status;
    Integer code;
    AuthbridgeReportResponse response;

    public AuthbridgeReportResponse getResponse() {
        return response;
    }

    public void setResponse(AuthbridgeReportResponse response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
