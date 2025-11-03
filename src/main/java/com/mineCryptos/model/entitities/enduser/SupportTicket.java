package com.mineCryptos.model.entitities.enduser;


import com.mineCryptos.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_ticket")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND ACTIVE_STATE_CODE_FK_ID='ACTIVE' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class SupportTicket extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUPPORT_TICKET_PK_ID")
    private Integer supportTicketPkId;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "PRIORITY")
    private String priority;

    @Column(name = "USER_NODE_ID")
    private String userNodeId;


    @Column(name = "MESSAGE", length = 1000)
    private String message;

    @Column(name = "STATUS")
    private String status;  // OPEN, IN_PROGRESS, CLOSED


    @Column(name = "UPDATED_AT_DATE_TIME")
    private LocalDateTime updatedAtDateTime;

    // Optional fields
    @Column(name = "TRANSACTION_PASSWORD")
    private String transactionPassword;

    @Column(name = "OTP")
    private String otp;

    @Transient
    private String userName;

    public Integer getSupportTicketPkId() {
        return supportTicketPkId;
    }

    public void setSupportTicketPkId(Integer supportTicketPkId) {
        this.supportTicketPkId = supportTicketPkId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAtDateTime() {
        return updatedAtDateTime;
    }

    public void setUpdatedAtDateTime(LocalDateTime updatedAtDateTime) {
        this.updatedAtDateTime = updatedAtDateTime;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(String userNodeId) {
        this.userNodeId = userNodeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
