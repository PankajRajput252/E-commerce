package com.gunwala.shipRocket.model;

public class AwbResponseData {
    private int shipment_id;
    private String awb_code;
    private int courier_company_id;
    private String courier_name;
    private String pickup_scheduled_date;
    private String routing_code;
    private long order_id;

    public int getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public String getAwb_code() {
        return awb_code;
    }

    public void setAwb_code(String awb_code) {
        this.awb_code = awb_code;
    }

    public int getCourier_company_id() {
        return courier_company_id;
    }

    public void setCourier_company_id(int courier_company_id) {
        this.courier_company_id = courier_company_id;
    }

    public String getCourier_name() {
        return courier_name;
    }

    public void setCourier_name(String courier_name) {
        this.courier_name = courier_name;
    }

    public String getPickup_scheduled_date() {
        return pickup_scheduled_date;
    }

    public void setPickup_scheduled_date(String pickup_scheduled_date) {
        this.pickup_scheduled_date = pickup_scheduled_date;
    }

    public String getRouting_code() {
        return routing_code;
    }

    public void setRouting_code(String routing_code) {
        this.routing_code = routing_code;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }
}
