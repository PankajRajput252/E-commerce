package com.gunwala.shipRocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Body for POST /courier/generate/pickup: {"shipment_id": [12345]} */
public class PickupRequestBody {

    @JsonProperty("shipment_id")
    private List<Long> shipmentId;

    public PickupRequestBody() {
    }

    public PickupRequestBody(Long shipmentId) {
        this.shipmentId = List.of(shipmentId);
    }

    public List<Long> getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(List<Long> shipmentId) {
        this.shipmentId = shipmentId;
    }
}
