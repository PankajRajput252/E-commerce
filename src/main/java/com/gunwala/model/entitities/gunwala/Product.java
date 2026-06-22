package com.gunwala.model.entitities.gunwala;


import com.gunwala.model.StandardFieldClass;
import org.hibernate.annotations.Where;

//import javax.persistence.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class Product extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_PK_ID")
    private  int productPkId;

    @Column(name = "TITLE")
    private  String title;

    @Column(name = "DESCRIPTION")
    private  String description;

//    @Column(name = "CATEGORY_ID")
//    private  int categoryId;
//
//    @Column(name = "SUB_CATEGORY_ID")
//    private  int subcategoryId;

    @Column(name = "WEAPON_CATEGORY_FK_ID")
    private int weaponCategoryFkId;

    @Column(name = "WEAPON_TYPE_FK_ID")
    private int weaponTypeFkId;

    @Column(name = "WEAPON_SUB_TYPE_FK_ID")
    private int weaponSubTypeFkId;

    @Column(name = "CALIBER_FK_ID")
    private int caliberFkId;

    @Column(name = "LICENSE_REQUIRED")
    private Boolean licenseRequired;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "IS_NEGOTIABLE")
    private boolean isNegotiable;

    @Column(name = "SELLER_ID")
    private String sellerId;

    @Column(name = "LOCATION")
    private String location ;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_AT")
    private String createdAt;

    @Column(name = "UPDATED_AT")
    private String updatedAt;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "IS_STORE_PRODUCT")
    private boolean isStoreProduct;

    @Transient
    private List<ProductImage> productImageList;

    public int getProductPkId() {
        return productPkId;
    }

    public void setProductPkId(int productPkId) {
        this.productPkId = productPkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public int getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(int categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public int getSubcategoryId() {
//        return subcategoryId;
//    }
//
//    public void setSubcategoryId(int subcategoryId) {
//        this.subcategoryId = subcategoryId;
//    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isNegotiable() {
        return isNegotiable;
    }

    public void setNegotiable(boolean negotiable) {
        isNegotiable = negotiable;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<ProductImage> getProductImageList() {
        return productImageList;
    }

    public void setProductImageList(List<ProductImage> productImageList) {
        this.productImageList = productImageList;
    }

    public boolean getIsStoreProduct() {
        return isStoreProduct;
    }

    public void setIsStoreProduct(boolean isStoreProduct) {
        this.isStoreProduct = isStoreProduct;
    }

    public int getWeaponCategoryFkId() {
        return weaponCategoryFkId;
    }

    public void setWeaponCategoryFkId(int weaponCategoryFkId) {
        this.weaponCategoryFkId = weaponCategoryFkId;
    }

    public int getWeaponTypeFkId() {
        return weaponTypeFkId;
    }

    public void setWeaponTypeFkId(int weaponTypeFkId) {
        this.weaponTypeFkId = weaponTypeFkId;
    }

    public int getWeaponSubTypeFkId() {
        return weaponSubTypeFkId;
    }

    public void setWeaponSubTypeFkId(int weaponSubTypeFkId) {
        this.weaponSubTypeFkId = weaponSubTypeFkId;
    }

    public int getCaliberFkId() {
        return caliberFkId;
    }

    public void setCaliberFkId(int caliberFkId) {
        this.caliberFkId = caliberFkId;
    }

    public Boolean getIsLicenseRequired() {
        return licenseRequired;
    }

    public void setIsLicenseRequired(Boolean isLicenseRequired) {
        this.licenseRequired = isLicenseRequired;
    }
}
