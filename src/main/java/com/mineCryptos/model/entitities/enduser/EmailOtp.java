package com.mineCryptos.model.entitities.enduser;

import javax.persistence.*;

@Entity
@Table(name = "email_otp")
public class EmailOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMAIL_OTP_PK_ID")
    private Integer emailOtpPkId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "OTP")
    private String otp;

    @Column(name = "EXPIRY_TIME")
    private long expiryTime;

    @Column(name = "USER_NODEID")
    private String userNodeId;

    @Column(name = "CREATED_DATETIME")
    private String createdDatetime;

    public Integer getEmailOtpPkId() {
        return emailOtpPkId;
    }

    public void setEmailOtpPkId(Integer emailOtpPkId) {
        this.emailOtpPkId = emailOtpPkId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(String createdDatetime) {
        this.createdDatetime = createdDatetime;
    }
}
