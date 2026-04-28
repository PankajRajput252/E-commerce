package com.gunwala.model.entitities.gunwala;


import com.gunwala.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "category")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class Category extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CATEGORY_PK_ID")
    private  int categoryPkId;

    @Column(name = "CATEGORY_NAME")
    private  String categoryName;

    public int getCategoryPkId() {
        return categoryPkId;
    }

    public void setCategoryPkId(int categoryPkId) {
        this.categoryPkId = categoryPkId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
