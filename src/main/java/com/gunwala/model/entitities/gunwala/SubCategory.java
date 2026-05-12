package com.gunwala.model.entitities.gunwala;
import jakarta.persistence.*;

@Entity
@Table(name = "sub_category")
public class SubCategory {

    @Id
    @Column(name = "SUB_CATEGORY_PK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subCategoryPkId;

    @Column(name = "CATEGORY_FK_ID")
    private Integer categoryFkId;

    @Column(name = "SUB_CATEGORY_NAME")
    private String subCategoryName;

    public Integer getSubCategoryPkId() {
        return subCategoryPkId;
    }

    public void setSubCategoryPkId(Integer subCategoryPkId) {
        this.subCategoryPkId = subCategoryPkId;
    }

    public Integer getCategoryFkId() {
        return categoryFkId;
    }

    public void setCategoryFkId(Integer categoryFkId) {
        this.categoryFkId = categoryFkId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
