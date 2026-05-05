package com.gunwala.model.entitities.gunwala;

public class DashBoardDetails {
    private Integer totalUser;
    private Integer activeUser;
    private Integer inActiveUser;
    private Integer totalProduct;
    private Integer normalUser;
    private Integer premiumUser;
    private Integer totalAdminUser;

    public Integer getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Integer totalUser) {
        this.totalUser = totalUser;
    }

    public Integer getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(Integer activeUser) {
        this.activeUser = activeUser;
    }

    public Integer getInActiveUser() {
        return inActiveUser;
    }

    public void setInActiveUser(Integer inActiveUser) {
        this.inActiveUser = inActiveUser;
    }

    public Integer getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(Integer totalProduct) {
        this.totalProduct = totalProduct;
    }

    public Integer getNormalUser() {
        return normalUser;
    }

    public void setNormalUser(Integer normalUser) {
        this.normalUser = normalUser;
    }

    public Integer getPremiumUser() {
        return premiumUser;
    }

    public void setPremiumUser(Integer premiumUser) {
        this.premiumUser = premiumUser;
    }

    public Integer getTotalAdminUser() {
        return totalAdminUser;
    }

    public void setTotalAdminUser(Integer totalAdminUser) {
        this.totalAdminUser = totalAdminUser;
    }
}
