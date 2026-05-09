package com.gunwala.shipRocket.model;

public class PickupResponse {
    private Integer pickup_status;
    private PickupData response;

    public Integer getPickup_status() {
        return pickup_status;
    }

    public void setPickup_status(Integer pickup_status) {
        this.pickup_status = pickup_status;
    }

    public PickupData getResponse() {
        return response;
    }

    public void setResponse(PickupData response) {
        this.response = response;
    }
}
