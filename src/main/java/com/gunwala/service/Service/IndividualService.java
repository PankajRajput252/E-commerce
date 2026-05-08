package com.gunwala.service.Service;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.admin.SubscriptionDefinition;
import com.gunwala.model.entitities.enduser.SupportTicket;
import com.gunwala.model.entitities.gunwala.*;

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

    FinalResponse getUserVisit(Integer userVisitPkId, String userFkId);

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

    FinalResponse getShiprocketTokenResponse();

    FinalResponse getShiprocketOrderResponse();
}
