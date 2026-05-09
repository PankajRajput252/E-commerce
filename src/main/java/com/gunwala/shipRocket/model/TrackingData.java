package com.gunwala.shipRocket.model;

import java.util.List;

public class TrackingData {
    private Integer track_status;
    private Integer shipment_status;
    private List<ShipmentTrack> shipment_track;
    private Object shipment_track_activities;
    private String track_url;
    private String qc_response;
    private List<String> qc_img_url;
    private Boolean is_return;
    private String error;
    private String order_tag;
    private Object npr;
    private Object ndr;

    public Integer getTrack_status() {
        return track_status;
    }

    public void setTrack_status(Integer track_status) {
        this.track_status = track_status;
    }

    public Integer getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(Integer shipment_status) {
        this.shipment_status = shipment_status;
    }

    public List<ShipmentTrack> getShipment_track() {
        return shipment_track;
    }

    public void setShipment_track(List<ShipmentTrack> shipment_track) {
        this.shipment_track = shipment_track;
    }

    public Object getShipment_track_activities() {
        return shipment_track_activities;
    }

    public void setShipment_track_activities(Object shipment_track_activities) {
        this.shipment_track_activities = shipment_track_activities;
    }

    public String getTrack_url() {
        return track_url;
    }

    public void setTrack_url(String track_url) {
        this.track_url = track_url;
    }

    public String getQc_response() {
        return qc_response;
    }

    public void setQc_response(String qc_response) {
        this.qc_response = qc_response;
    }

    public List<String> getQc_img_url() {
        return qc_img_url;
    }

    public void setQc_img_url(List<String> qc_img_url) {
        this.qc_img_url = qc_img_url;
    }

    public Boolean getIs_return() {
        return is_return;
    }

    public void setIs_return(Boolean is_return) {
        this.is_return = is_return;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOrder_tag() {
        return order_tag;
    }

    public void setOrder_tag(String order_tag) {
        this.order_tag = order_tag;
    }

    public Object getNpr() {
        return npr;
    }

    public void setNpr(Object npr) {
        this.npr = npr;
    }

    public Object getNdr() {
        return ndr;
    }

    public void setNdr(Object ndr) {
        this.ndr = ndr;
    }
}
