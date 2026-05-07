package com.gunwala.model.entitities.gunwala;

import com.gunwala.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;

@Entity
@Table(name = "product_image")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class ProductImage extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_IMAGE_PK_ID")
    private  int productImagePkId;

    @Column(name = "PRODUCT_IMAGE_ID")
    private String productImageId;

    @Column(name = "PRODUCT_Fk_ID")
    private int productFkId;

    @Column(name = "PROFILE_IMAGE_URL",length = 1000)
    private String profileImageUrl;

    public int getProductImagePkId() {
        return productImagePkId;
    }

    public void setProductImagePkId(int productImagePkId) {
        this.productImagePkId = productImagePkId;
    }

    public String getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(String productImageId) {
        this.productImageId = productImageId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getProductFkId() {
        return productFkId;
    }

    public void setProductFkId(int productFkId) {
        this.productFkId = productFkId;
    }
}
