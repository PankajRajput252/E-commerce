package com.gunwala.model.entitities.gunwala;


import com.gunwala.model.StandardFieldClass;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "product_attribute")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class ProductAttribute extends StandardFieldClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_ATTRIBUTE_PK_ID")
    private  int productAttributePkId;

    @Column(name = "PRODUCT_ID")
    private  int productId;

    @Column(name = "ATTRIBUTE_NAME")
    private  String attributeName;

    @Column(name = "ATTRIBUTE_VALUE")
    private  String attributevalue ;

    public int getProductAttributePkId() {
        return productAttributePkId;
    }

    public void setProductAttributePkId(int productAttributePkId) {
        this.productAttributePkId = productAttributePkId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributevalue() {
        return attributevalue;
    }

    public void setAttributevalue(String attributevalue) {
        this.attributevalue = attributevalue;
    }
}
