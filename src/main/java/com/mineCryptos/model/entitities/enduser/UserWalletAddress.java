package com.mineCryptos.model.entitities.enduser;

import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "user_wallet_address")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class UserWalletAddress extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_WALLET_ADDRESS_PK_ID")
    private Integer userWalletAddressPkId;

    @Column(name = "USER_NODE_Id")
    private String userNodeId;

    @Column(name = "WALLET_TYPE")
    private String walletType;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "IS_VERIFIED")
    private boolean isVerified;

    @Column(name = "IS_DEFAULT")
    private boolean isDefault;

    @Column(name = "OTP_HASH")
    private String otpHash;

    @Column(name = "OTP_EXPIRES_AT")
    private String otpExpiresAt;

    @Column(name = "UPDATED_AT")
    private String updatedAt;

    public Integer getUserWalletAddressPkId() {
        return userWalletAddressPkId;
    }

    public void setUserWalletAddressPkId(Integer userWalletAddressPkId) {
        this.userWalletAddressPkId = userWalletAddressPkId;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean verified) {
        this.isVerified = verified;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public String getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(String otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
