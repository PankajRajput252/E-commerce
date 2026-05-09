package com.gunwala.shipRocket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class AwbResponseAssaign {

    @JsonProperty("awb_assign_status")
    private int awb_assign_status;

    private AwbResponse response;

    @JsonProperty("no_pickup_popup")
    private int no_pickup_popup;

    @JsonProperty("quick_pick")
    private int quick_pick;


    public int getAwb_assign_status() {
        return awb_assign_status;
    }

    public void setAwb_assign_status(int awb_assign_status) {
        this.awb_assign_status = awb_assign_status;
    }

    public AwbResponse getResponse() {
        return response;
    }

    public void setResponse(AwbResponse response) {
        this.response = response;
    }
}
