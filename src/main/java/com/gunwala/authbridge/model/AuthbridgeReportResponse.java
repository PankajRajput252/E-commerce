package com.gunwala.authbridge.model;

import java.util.List;

public class AuthbridgeReportResponse {

    String uniqueID;
    String requestId;
    String message;
    String form_status;
    String caseARS;
    String CaseDueDate;
    String caseStatus;
    String caseSeverity;
    List<AuthbridgeReportResponseCheck> checks;

    public List<AuthbridgeReportResponseCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<AuthbridgeReportResponseCheck> checks) {
        this.checks = checks;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getForm_status() {
        return form_status;
    }

    public void setForm_status(String form_status) {
        this.form_status = form_status;
    }

    public String getCaseARS() {
        return caseARS;
    }

    public void setCaseARS(String caseARS) {
        this.caseARS = caseARS;
    }

    public String getCaseDueDate() {
        return CaseDueDate;
    }

    public void setCaseDueDate(String caseDueDate) {
        CaseDueDate = caseDueDate;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCaseSeverity() {
        return caseSeverity;
    }

    public void setCaseSeverity(String caseSeverity) {
        this.caseSeverity = caseSeverity;
    }
}
