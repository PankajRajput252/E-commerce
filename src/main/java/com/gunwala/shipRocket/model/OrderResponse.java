package com.gunwala.shipRocket.model;

public class OrderResponse {
    private Long order_id;
    private String channel_order_id;
    private Long shipment_id;
    private String status;
    private Integer status_code;
    private Integer onboarding_completed_now;
    private String awb_code;
    private String courier_company_id;
    private String courier_name;
    private Boolean new_channel;
    private String packaging_box_error;

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getChannel_order_id() {
        return channel_order_id;
    }

    public void setChannel_order_id(String channel_order_id) {
        this.channel_order_id = channel_order_id;
    }

    public Long getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(Long shipment_id) {
        this.shipment_id = shipment_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public Integer getOnboarding_completed_now() {
        return onboarding_completed_now;
    }

    public void setOnboarding_completed_now(Integer onboarding_completed_now) {
        this.onboarding_completed_now = onboarding_completed_now;
    }

    public String getAwb_code() {
        return awb_code;
    }

    public void setAwb_code(String awb_code) {
        this.awb_code = awb_code;
    }

    public String getCourier_company_id() {
        return courier_company_id;
    }

    public void setCourier_company_id(String courier_company_id) {
        this.courier_company_id = courier_company_id;
    }

    public String getCourier_name() {
        return courier_name;
    }

    public void setCourier_name(String courier_name) {
        this.courier_name = courier_name;
    }

    public Boolean getNew_channel() {
        return new_channel;
    }

    public void setNew_channel(Boolean new_channel) {
        this.new_channel = new_channel;
    }

    public String getPackaging_box_error() {
        return packaging_box_error;
    }

    public void setPackaging_box_error(String packaging_box_error) {
        this.packaging_box_error = packaging_box_error;
    }
}
