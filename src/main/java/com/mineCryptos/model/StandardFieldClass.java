package com.mineCryptos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class StandardFieldClass {

    @Column(name = "NOTES_G11N_BIG_TXT")
    private String notesG11nBigTxt;

    @Column(name = "EFFECTIVE_DATETIME")
    private String effectiveDateTime;

    @Column(name = "SAVE_STATE_CODE_FK_ID")
    private String saveStateCodeFkId;

    @Column(name = "ACTIVE_STATE_CODE_FK_ID" , columnDefinition ="VARCHAR(200) default 'ACTIVE'")
    private String activeStateCodeFkId;

    @Column(name = "RECORD_STATE_CODE_FK_ID")
    private String recordStateCodeFkId;

    @Column(name = "IS_DELETED")
    private boolean deleted;

    //@CreationTimestamp
    @Column(name = "CREATED_DATETIME")
    private String createdDatetime;

    @Column(name = "LAST_MODIFIED_DATETIME")
    private String lastModifiedDateTime;


    // for eg in some cases if we don't want dirty update to be checked than set it to true
    @Transient
    private boolean genericFlag;


    public StandardFieldClass() {
    }

    public StandardFieldClass(String notesG11nBigTxt, String effectiveDateTime, String saveStateCodeFkId, String activeStateCodeFkId, String recordStateCodeFkId, boolean isDeleted, String createdDatetime) {
        this.notesG11nBigTxt = notesG11nBigTxt;
        this.effectiveDateTime = effectiveDateTime;
        this.saveStateCodeFkId = saveStateCodeFkId;
        this.activeStateCodeFkId = activeStateCodeFkId;
        this.recordStateCodeFkId = recordStateCodeFkId;
        this.deleted = isDeleted;
        this.createdDatetime = createdDatetime;

    }

    public String getNotesG11nBigTxt() {
        return notesG11nBigTxt;
    }

    public void setNotesG11nBigTxt(String notesG11nBigTxt) {
        this.notesG11nBigTxt = notesG11nBigTxt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime =  Util.getEffectiveDateTime(effectiveDateTime);
    }

    public String getSaveStateCodeFkId() {
        return saveStateCodeFkId;
    }

    public void setSaveStateCodeFkId(String saveStateCodeFkId) {
        this.saveStateCodeFkId = saveStateCodeFkId;
    }

    public String getActiveStateCodeFkId() {
        return activeStateCodeFkId;
    }

    public void setActiveStateCodeFkId(String activeStateCodeFkId) {
        this.activeStateCodeFkId = activeStateCodeFkId;
    }

    public String getRecordStateCodeFkId() {
        return recordStateCodeFkId;
    }

    public void setRecordStateCodeFkId(String recordStateCodeFkId) {
        this.recordStateCodeFkId = recordStateCodeFkId;
    }

    public boolean getIsDeleted() {
        return deleted;
    }

    public void setIsDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(String createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @PrePersist
    private void prePersistFunction(){
        if(StringUtils.isEmpty(activeStateCodeFkId)){
            activeStateCodeFkId = "ACTIVE";
        }
        if(StringUtils.isEmpty(recordStateCodeFkId)){
            recordStateCodeFkId = "CURRENT";
        }
        if(StringUtils.isEmpty(saveStateCodeFkId)){
            saveStateCodeFkId = "SAVED";
        }
        if(StringUtils.isEmpty(deleted)){
            deleted = false;
        }
    }

    public boolean getIsGenericFlag() {
        return genericFlag;
    }

    public void setIsGenericFlag(boolean genericFlag) {
        this.genericFlag = genericFlag;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }
}
