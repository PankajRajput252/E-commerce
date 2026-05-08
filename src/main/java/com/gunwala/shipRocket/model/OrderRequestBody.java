package com.gunwala.shipRocket.model;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestBody {

    private Integer order_id;
    private String order_date;
    private Integer sub_total;
    private Integer shipping_is_billing;
    private String billing_customer_name;
    private String billing_last_name;
    private String billing_address;
    private String billing_state;
    private String billing_country;
    private String billing_phone;
    private String billing_pincode;
    private String payment_method;
    private Double weight;
    private Integer length;
    private Integer breadth;
    private Integer height;

    ShippingDetails shipping_details;
    List<OrderItems> order_items;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public Integer getSub_total() {
        return sub_total;
    }

    public void setSub_total(Integer sub_total) {
        this.sub_total = sub_total;
    }

    public Integer getShipping_is_billing() {
        return shipping_is_billing;
    }

    public void setShipping_is_billing(Integer shipping_is_billing) {
        this.shipping_is_billing = shipping_is_billing;
    }

    public String getBilling_customer_name() {
        return billing_customer_name;
    }

    public void setBilling_customer_name(String billing_customer_name) {
        this.billing_customer_name = billing_customer_name;
    }

    public String getBilling_last_name() {
        return billing_last_name;
    }

    public void setBilling_last_name(String billing_last_name) {
        this.billing_last_name = billing_last_name;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getBilling_state() {
        return billing_state;
    }

    public void setBilling_state(String billing_state) {
        this.billing_state = billing_state;
    }

    public String getBilling_country() {
        return billing_country;
    }

    public void setBilling_country(String billing_country) {
        this.billing_country = billing_country;
    }

    public String getBilling_phone() {
        return billing_phone;
    }

    public void setBilling_phone(String billing_phone) {
        this.billing_phone = billing_phone;
    }

    public String getBilling_pincode() {
        return billing_pincode;
    }

    public void setBilling_pincode(String billing_pincode) {
        this.billing_pincode = billing_pincode;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getBreadth() {
        return breadth;
    }

    public void setBreadth(Integer breadth) {
        this.breadth = breadth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ShippingDetails getShipping_details() {
        return shipping_details;
    }

    public void setShipping_details(ShippingDetails shipping_details) {
        this.shipping_details = shipping_details;
    }

    public List<OrderItems> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<OrderItems> order_items) {
        this.order_items = order_items;
    }
}
