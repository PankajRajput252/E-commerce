package com.gunwala.shipRocket.model;

public class OrderItems {
    private String name;
    private String sku;
    private Integer units;
    private String selling_price;
    private String discount;
    private String tax;
    private Long hsn;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public Long getHsn() {
        return hsn;
    }

    public void setHsn(Long hsn) {
        this.hsn = hsn;
    }
}
