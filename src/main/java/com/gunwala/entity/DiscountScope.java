package com.gunwala.entity;

/** Which products an automatic sale discount covers. Only store products are ever discounted. */
public enum DiscountScope {
    ALL_STORE_PRODUCTS,
    SELECTED_PRODUCTS
}
