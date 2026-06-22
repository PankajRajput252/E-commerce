package com.gunwala.service.Service;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.admin.SubscriptionDefinition;
import com.gunwala.model.entitities.enduser.SupportTicket;
import com.gunwala.model.entitities.gunwala.*;
import com.gunwala.shipRocket.model.AwbRequestBody;
import com.gunwala.shipRocket.model.OrderRequestBody;

public interface IndividualService {
    FinalResponse addProduct(Product product);

    FinalResponse getProduct(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue,Integer categoryId);

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

    FinalResponse getFavorites(Integer favoritesPkIdInt, String userFkIdInt, Integer productFkIdInt);

    FinalResponse postFavorites(Favorites favorites);

    FinalResponse updateFavorrites(Favorites favorites);

    FinalResponse deleteFavorites(Integer favoritePkId);

    FinalResponse getDashBoardDetail();

    FinalResponse getUserWallet(Integer userWalletPkId, String userFkId);

    FinalResponse postUserWallet(UserWallet userWallet);

    FinalResponse deleteUserWallet(Integer userWalletPkId, String userFkId);

    FinalResponse putUserWallet(UserWallet userWallet);

    FinalResponse getUserVisit(String userVisitPkId, String userFkId,String productFkId);

    FinalResponse deleteUserVisit(Integer userVisitPkId);

    FinalResponse postUserVisit(UserVisit userVisit);

    FinalResponse putUserVisit(UserVisit userVisit);

    FinalResponse getUserReview(Integer userReviewPkId, String userFkId, Integer productFkId);

    FinalResponse deleteUserReview(Integer userReviewPkId);

    FinalResponse postUserReview(UserReview userReview);

    FinalResponse putUserReview(UserReview userReview);

    FinalResponse generateAuthBridgeToken();

    FinalResponse generateAuthbridgeReportDetail(String requestId);

    FinalResponse generateOrderTokenResponse();

    FinalResponse createShiprocketOrder(OrderRequestBody orderRequestBody);

    FinalResponse generateAwb(AwbRequestBody awbRequestBody);

    FinalResponse generatePickup(String shipmentId);

    FinalResponse trackShipment(String shipmentId);

    FinalResponse getSubCategory(String subCategoryPkId, String categoryFkId);

    FinalResponse deleteSubCategory(String subCategoryPkId, String categoryFkId);

    FinalResponse postSubCategory(SubCategory subCategory);

    FinalResponse putSubCategory(SubCategory subCategory);

    FinalResponse getWeaponType(Integer weaponTypePkIdInt, Integer weaponCategoryFkIdInt, int page, int size);

    FinalResponse updateWeaponType(Integer id, WeaponType weaponType);

    FinalResponse deleteWeaponType(Integer id);

    FinalResponse addWeaponType(WeaponType weaponType);
}
