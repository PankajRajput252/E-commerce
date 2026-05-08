package com.gunwala.shipRocket.model;

public class order_items {
    private String sku;
    private String name;
    private Integer units;
    private Integer selling_price;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(Integer selling_price) {
        this.selling_price = selling_price;
    }
}
