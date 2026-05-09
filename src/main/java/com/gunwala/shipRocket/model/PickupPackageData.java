package com.gunwala.shipRocket.model;

public class PickupPackageData {
    private Long shipment_id;
    private String status;

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
}
