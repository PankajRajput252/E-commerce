package com.gunwala.authbridge.model;

public class Response {
    long expiryTimeStamp;
    String token;

    public long getExpiryTimeStamp() {

        return expiryTimeStamp;
    }

    public void setExpiryTimeStamp(long expiryTimeStamp) {
        this.expiryTimeStamp = expiryTimeStamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
