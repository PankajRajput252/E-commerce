package com.gunwala.shipRocket.model;

public class ShipmentTrack {
    private Integer id;
    private String awb_code;
    private Object courier_company_id;
    private Integer shipment_id;
    private Integer order_id;
    private String pickup_date;
    private String delivered_date;
    private String weight;
    private Integer packages;
    private String current_status;
    private Integer current_status_id;
    private String delivered_to;
    private String destination;
    private String consignee_name;
    private String origin;
    private Object courier_agent_details;
    private String courier_name;
    private Object edd;
    private String pod;
    private String pod_status;
    private String rto_delivered_date;
    private String return_awb_code;
    private String updated_time_stamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAwb_code() {
        return awb_code;
    }

    public void setAwb_code(String awb_code) {
        this.awb_code = awb_code;
    }

    public Object getCourier_company_id() {
        return courier_company_id;
    }

    public void setCourier_company_id(Object courier_company_id) {
        this.courier_company_id = courier_company_id;
    }

    public Integer getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(Integer shipment_id) {
        this.shipment_id = shipment_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getDelivered_date() {
        return delivered_date;
    }

    public void setDelivered_date(String delivered_date) {
        this.delivered_date = delivered_date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Integer getPackages() {
        return packages;
    }

    public void setPackages(Integer packages) {
        this.packages = packages;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public Integer getCurrent_status_id() {
        return current_status_id;
    }

    public void setCurrent_status_id(Integer current_status_id) {
        this.current_status_id = current_status_id;
    }

    public String getDelivered_to() {
        return delivered_to;
    }

    public void setDelivered_to(String delivered_to) {
        this.delivered_to = delivered_to;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getConsignee_name() {
        return consignee_name;
    }

    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Object getCourier_agent_details() {
        return courier_agent_details;
    }

    public void setCourier_agent_details(Object courier_agent_details) {
        this.courier_agent_details = courier_agent_details;
    }

    public String getCourier_name() {
        return courier_name;
    }

    public void setCourier_name(String courier_name) {
        this.courier_name = courier_name;
    }

    public Object getEdd() {
        return edd;
    }

    public void setEdd(Object edd) {
        this.edd = edd;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPod_status() {
        return pod_status;
    }

    public void setPod_status(String pod_status) {
        this.pod_status = pod_status;
    }

    public String getRto_delivered_date() {
        return rto_delivered_date;
    }

    public void setRto_delivered_date(String rto_delivered_date) {
        this.rto_delivered_date = rto_delivered_date;
    }

    public String getReturn_awb_code() {
        return return_awb_code;
    }

    public void setReturn_awb_code(String return_awb_code) {
        this.return_awb_code = return_awb_code;
    }

    public String getUpdated_time_stamp() {
        return updated_time_stamp;
    }

    public void setUpdated_time_stamp(String updated_time_stamp) {
        this.updated_time_stamp = updated_time_stamp;
    }
}
