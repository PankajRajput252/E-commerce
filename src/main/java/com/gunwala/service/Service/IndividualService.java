package com.gunwala.service.Service;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.admin.SubscriptionDefinition;
import com.gunwala.model.entitities.enduser.SupportTicket;
import com.gunwala.model.entitities.gunwala.Category;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.model.entitities.gunwala.ProductImage;

public interface IndividualService {
    FinalResponse addProduct(Product product);

    FinalResponse getProduct(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse deleteProduct(Integer id);

    FinalResponse updateProduct(Integer id, Product product);

    FinalResponse addCategory(Category category);

    FinalResponse getCategory(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse deleteCategory(Integer id);

    FinalResponse updateCategory(Integer id, Category category);

    FinalResponse addProductImage(ProductImage productImage);

    FinalResponse getProductImage(Integer inputPkIdInt, Integer inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse deleteProductImage(Integer id);

    FinalResponse updateProductImage(Integer id, ProductImage productImage);

    FinalResponse addSubscriptionDefinition(SubscriptionDefinition subscriptionDefinition);

    FinalResponse getSubscriptionDefinition(Integer inputPkIdInt, String inputFkIdInt, int page, int size, String filterBy, String searchValue);

    FinalResponse deleteSubscriptionDefinition(Integer id);

    FinalResponse updateSubscriptionDefinition(Integer id, SubscriptionDefinition subscriptionDefinition);

    FinalResponse getSupportTicket(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue);

    FinalResponse addSupportTicket(SupportTicket supportTicket);

    FinalResponse updateSupportTicket(Integer id, SupportTicket supportTicket);

    FinalResponse resolveSupport(Integer id);

    FinalResponse deleteSupportTicket(Integer id);
}
