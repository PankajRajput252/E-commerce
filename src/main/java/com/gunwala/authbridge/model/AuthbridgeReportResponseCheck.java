package com.gunwala.authbridge.model;

public class AuthbridgeReportResponseCheck {
    String checkUID;
    String type;
    String CheckName;
    String severity;
    String disposition;
    String status;
    String CheckDueDate;
    String comments;

    public String getCheckUID() {
        return checkUID;
    }

    public void setCheckUID(String checkUID) {
        this.checkUID = checkUID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheckName() {
        return CheckName;
    }

    public void setCheckName(String checkName) {
        CheckName = checkName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckDueDate() {
        return CheckDueDate;
    }

    public void setCheckDueDate(String checkDueDate) {
        CheckDueDate = checkDueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
