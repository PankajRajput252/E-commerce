package com.mineCryptos.model.entitities.enduser;

public class Profile {
    private String userName;
    private String email;
    private String country;
    private String mobile;
    private String transactionPassword;
    private String userNodeId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }
}
