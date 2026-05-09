package com.gunwala.shipRocket.model;

public class PickupData {
    private String pickup_scheduled_date;
    private String pickup_token_number;
    private String status;
    private String pickup_generated_date;
    private PickupInnerData data;

    public String getPickup_scheduled_date() {
        return pickup_scheduled_date;
    }

    public void setPickup_scheduled_date(String pickup_scheduled_date) {
        this.pickup_scheduled_date = pickup_scheduled_date;
    }

    public String getPickup_token_number() {
        return pickup_token_number;
    }

    public void setPickup_token_number(String pickup_token_number) {
        this.pickup_token_number = pickup_token_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickup_generated_date() {
        return pickup_generated_date;
    }

    public void setPickup_generated_date(String pickup_generated_date) {
        this.pickup_generated_date = pickup_generated_date;
    }

    public PickupInnerData getData() {
        return data;
    }

    public void setData(PickupInnerData data) {
        this.data = data;
    }
}
