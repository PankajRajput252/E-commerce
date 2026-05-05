package com.gunwala.model.entitities.gunwala;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FAVORITES_PK_ID")
    private Integer favoritesPkId;

    @Column(name = "USER_FK_ID")
    private String userFkId;

    @Column(name="PRODUCT_FK_ID")
    private Integer productFkId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Transient
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getFavoritesPkId() {
        return favoritesPkId;
    }

    public void setFavoritesPkId(Integer favoritesPkId) {
        this.favoritesPkId = favoritesPkId;
    }

    public String getUserFkId() {
        return userFkId;
    }

    public void setUserFkId(String userFkId) {
        this.userFkId = userFkId;
    }

    public Integer getProductFkId() {
        return productFkId;
    }

    public void setProductFkId(Integer productFkId) {
        this.productFkId = productFkId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
