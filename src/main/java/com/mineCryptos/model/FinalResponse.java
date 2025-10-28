package com.mineCryptos.model;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class FinalResponse<E> {
    public String id;
    public UUID uuid;
    public String name;
    public String status;
    public boolean check;
    public String statusCode;
    public long timeStamp;
    public int count;
    public URL videoUrl;
    public URL attechUrl;
    public List<E> data;
    public Map<String, Integer> map;
    public Map<String, String> stringMap;
    public String message;
    public E response;
    public String versionId;
    public String lastModifiedDateTime;
    public Map<String, Object> relationShipMap;
    public String personid;
    public String positionId;
    public String relationshipType;
    public BigDecimal bigDecimal;
    public Double doubleValue;
    public Map<Object, Object> commonMap;
    public E warningResponse;

    // public PolicyActions policyActions;
    public boolean isLoginPersonIsPHManager;
    public boolean isLoginPersonIsAdmin;
    public List<String> companyList;
    public List<String> regionList;
    private int countSize;





    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   /* public PolicyActions getPolicyActions() {
        return policyActions;
    }

    public void setPolicyActions(PolicyActions policyActions) {
        this.policyActions = policyActions;
    }*/

    public E getResponse() {
        return response;
    }

    public void setResponse(E response) {
        this.response = response;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public URL getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(URL videoUrl) {
        this.videoUrl = videoUrl;
    }

    public URL getAttechUrl() {
        return attechUrl;
    }

    public void setAttechUrl(URL attechUrl) {
        this.attechUrl = attechUrl;
    }

    public Map<String, Object> getRelationShipMap() {
        return relationShipMap;
    }

    public void setRelationShipMap(Map<String, Object> relationShipMap) {
        this.relationShipMap = relationShipMap;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Map<Object, Object> getCommonMap() {
        return commonMap;
    }

    public void setCommonMap(Map<Object, Object> commonMap) {
        this.commonMap = commonMap;
    }

    public E getWarningResponse() {
        return warningResponse;
    }

    public void setWarningResponse(E warningResponse) {
        this.warningResponse = warningResponse;
    }



    public boolean getIsLoginPersonIsPHManager() {
        return isLoginPersonIsPHManager;
    }

    public void setIsLoginPersonIsPHManager(boolean isLoginPersonIsPHManager) {
        this.isLoginPersonIsPHManager = isLoginPersonIsPHManager;
    }

    public boolean getIsLoginPersonIsAdmin() {
        return isLoginPersonIsAdmin;
    }

    public void setIsLoginPersonIsAdmin(boolean isLoginPersonIsAdmin) {
        this.isLoginPersonIsAdmin = isLoginPersonIsAdmin;
    }

    public List<String> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<String> companyList) {
        this.companyList = companyList;
    }

    public List<String> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<String> regionList) {
        this.regionList = regionList;
    }

    public int getCountSize() {
        return countSize;
    }

    public void setCountSize(int countSize) {
        this.countSize = countSize;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

}
