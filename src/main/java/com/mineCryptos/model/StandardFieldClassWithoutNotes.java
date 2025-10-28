package com.mineCryptos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public class StandardFieldClassWithoutNotes {
    @Column(name = "EFFECTIVE_DATETIME")
    private String effectiveDatetime;

    @Column(name = "SAVE_STATE_CODE_FK_ID")
    private String saveStateCodeFkId;

    @Column(name = "ACTIVE_STATE_CODE_FK_ID" )
    private String activeStateCodeFkId;

    @Column(name = "RECORD_STATE_CODE_FK_ID")
    private String recordStateCodeFkId;

    @Column(name = "IS_DELETED")
    private boolean deleted;
    @Column(name = "CREATEDBY_PERSON_FK_ID")
    private UUID createbyPersonFkId;

    @Column(name = "CREATED_BY_PERSON_FULLNAME_G11N_BIG_TXT")
    private String createbyPersonFullNameG11nBigTxt;

    @CreationTimestamp
    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createdDatetime;

    @Column(name = "LAST_MODIFIEDBY_PERSON_FK_ID")
    private UUID lastModifiedbyPersonFkId;

    @Column(name = "LAST_MODIFIEDBY_PERSON_FULLNAME_G11N_BIG_TXT")
    private String lastModifiedPersonFullNameG11nBigTxt;

    // this flag will help to add custom logic inside application
    // for eg in some cases if we don't want dirty update to be checked than set it to true
    @Transient
    private boolean genericFlag;

    public StandardFieldClassWithoutNotes(String effectiveDatetime, String saveStateCodeFkId, String activeStateCodeFkId, String recordStateCodeFkId, boolean deleted, UUID createbyPersonFkId, String createbyPersonFullNameG11nBigTxt, LocalDateTime createdDatetime, UUID lastModifiedbyPersonFkId, String lastModifiedPersonFullNameG11nBigTxt) {
        this.effectiveDatetime = effectiveDatetime;
        this.saveStateCodeFkId = saveStateCodeFkId;
        this.activeStateCodeFkId = activeStateCodeFkId;
        this.recordStateCodeFkId = recordStateCodeFkId;
        this.deleted = deleted;
        this.createbyPersonFkId = createbyPersonFkId;
        this.createbyPersonFullNameG11nBigTxt = createbyPersonFullNameG11nBigTxt;
        this.createdDatetime = createdDatetime;
        this.lastModifiedbyPersonFkId = lastModifiedbyPersonFkId;
        this.lastModifiedPersonFullNameG11nBigTxt = lastModifiedPersonFullNameG11nBigTxt;
    }

    public StandardFieldClassWithoutNotes() {
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public String getEffectiveDatetime() {
        return effectiveDatetime;
    }

    public void setEffectiveDatetime(String effectiveDatetime) {
        this.effectiveDatetime =  Util.getEffectiveDateTime(effectiveDatetime);
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

    public UUID getCreatebyPersonFkId() {
        return createbyPersonFkId;
    }

    public void setCreatebyPersonFkId(UUID createbyPersonFkId) {
        this.createbyPersonFkId = createbyPersonFkId;
    }

    public String getCreatebyPersonFullNameG11nBigTxt() {
        return createbyPersonFullNameG11nBigTxt;
    }

    public void setCreatebyPersonFullNameG11nBigTxt(String createbyPersonFullNameG11nBigTxt) {
        this.createbyPersonFullNameG11nBigTxt = createbyPersonFullNameG11nBigTxt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public UUID getLastModifiedbyPersonFkId() {
        return lastModifiedbyPersonFkId;
    }

    public void setLastModifiedbyPersonFkId(UUID lastModifiedbyPersonFkId) {
        this.lastModifiedbyPersonFkId = lastModifiedbyPersonFkId;
    }

    public String getLastModifiedPersonFullNameG11nBigTxt() {
        return lastModifiedPersonFullNameG11nBigTxt;
    }

    public void setLastModifiedPersonFullNameG11nBigTxt(String lastModifiedPersonFullNameG11nBigTxt) {
        this.lastModifiedPersonFullNameG11nBigTxt = lastModifiedPersonFullNameG11nBigTxt;
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
}
