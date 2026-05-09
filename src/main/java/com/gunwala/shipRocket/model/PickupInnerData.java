package com.gunwala.shipRocket.model;

import java.util.List;

public class PickupInnerData {
    private Integer pickup_id;
    private List<PickupPackageData> packages;

    public Integer getPickup_id() {
        return pickup_id;
    }

    public void setPickup_id(Integer pickup_id) {
        this.pickup_id = pickup_id;
    }

    public List<PickupPackageData> getPackages() {
        return packages;
    }

    public void setPackages(List<PickupPackageData> packages) {
        this.packages = packages;
    }
}
