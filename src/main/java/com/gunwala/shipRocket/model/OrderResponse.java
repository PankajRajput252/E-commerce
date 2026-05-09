package com.gunwala.shipRocket.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class OrderResponse {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("channel_order_id")
    private String channelOrderId;

    @JsonProperty("shipment_id")
    private Long shipmentId;

    private String status;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("onboarding_completed_now")
    private Integer onboardingCompletedNow;

    @JsonProperty("awb_code")
    private String awbCode;

    @JsonProperty("courier_company_id")
    private String courierCompanyId;

    @JsonProperty("courier_name")
    private String courierName;

    @JsonProperty("new_channel")
    private Boolean newChannel;

    @JsonProperty("packaging_box_error")
    private String packagingBoxError;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getOnboardingCompletedNow() {
        return onboardingCompletedNow;
    }

    public void setOnboardingCompletedNow(Integer onboardingCompletedNow) {
        this.onboardingCompletedNow = onboardingCompletedNow;
    }

    public String getAwbCode() {
        return awbCode;
    }

    public void setAwbCode(String awbCode) {
        this.awbCode = awbCode;
    }

    public String getCourierCompanyId() {
        return courierCompanyId;
    }

    public void setCourierCompanyId(String courierCompanyId) {
        this.courierCompanyId = courierCompanyId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public Boolean getNewChannel() {
        return newChannel;
    }

    public void setNewChannel(Boolean newChannel) {
        this.newChannel = newChannel;
    }

    public String getPackagingBoxError() {
        return packagingBoxError;
    }

    public void setPackagingBoxError(String packagingBoxError) {
        this.packagingBoxError = packagingBoxError;
    }
}
