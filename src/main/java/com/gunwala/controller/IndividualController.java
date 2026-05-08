package com.gunwala.controller;

import com.gunwala.FinalException;
import com.gunwala.model.FinalResponse;
import com.gunwala.model.User;
import com.gunwala.model.Util;
import com.gunwala.model.entitities.admin.SubscriptionDefinition;
import com.gunwala.model.entitities.enduser.SupportTicket;
import com.gunwala.model.entitities.gunwala.*;
import com.gunwala.service.Service.IndividualService;
import com.gunwala.shipRocket.model.OrderRequestBody;
import com.gunwala.shipRocket.model.ShiprocketTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @PostMapping("/addProduct")
    public FinalResponse addProduct(@RequestBody Product product) throws FinalException {
        return this.individualService.addProduct(product);

    }

    @GetMapping("/getProduct")
    public FinalResponse getProduct(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "categoryId", required = false) String categoryId
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        Integer categoryIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        if (Util.isDefined(categoryId)) {
            categoryIdInt = Util.convertStringToInteger(categoryId);
        }
        return individualService.getProduct(inputPkIdInt, inputFkId, page, size, filterBy, searchValue,categoryIdInt);
    }


    @DeleteMapping("/deleteProduct/{id}")
    FinalResponse deleteProduct(@PathVariable Integer id) throws FinalException {
        return this.individualService.deleteProduct(id);
    }

    @PutMapping({"/updateProduct/{id}"})
    public FinalResponse updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        return this.individualService.updateProduct(id, product);
    }

    @PostMapping("/addCategory")
    public FinalResponse addCategory(@RequestBody Category category) throws FinalException {
        return this.individualService.addCategory(category);

    }

    @GetMapping("/getCategory")
    public FinalResponse getCategory(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return individualService.getCategory(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }


    @DeleteMapping("/deleteCategory/{id}")
    FinalResponse deleteCategory(@PathVariable Integer id) throws FinalException {
        return this.individualService.deleteCategory(id);
    }

    @PutMapping({"/updateCategory/{id}"})
    public FinalResponse updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return this.individualService.updateCategory(id, category);
    }

    @PostMapping("/addProductImage")
    public FinalResponse addProductImage(@RequestBody ProductImage productImage) throws FinalException {
        return this.individualService.addProductImage(productImage);

    }

    @GetMapping("/getProductImage")
    public FinalResponse getProductImage(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        if (Util.isDefined(inputPkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getProductImage(inputPkIdInt, inputFkIdInt, page, size, filterBy, searchValue);
    }


    @DeleteMapping("/deleteProductImage/{id}")
    FinalResponse deleteProductImage(@PathVariable Integer id) throws FinalException {
        return this.individualService.deleteProductImage(id);
    }

    @PutMapping({"/updateProductImage/{id}"})
    public FinalResponse updateProductImage(@PathVariable Integer id, @RequestBody ProductImage productImage) {
        return this.individualService.updateProductImage(id, productImage);
    }



    @PostMapping("/addSubscriptionDefinition")
    public FinalResponse addSubscriptionDefinition(@RequestBody SubscriptionDefinition subscriptionDefinition) throws FinalException {
        return this.individualService.addSubscriptionDefinition(subscriptionDefinition);

    }

    @GetMapping("/getSubscriptionDefinition")
    public FinalResponse getSubscriptionDefinition(
            @RequestParam(value = "inputPkId", required = false) String inputPkId,
            @RequestParam(value = "inputFkId", required = false) String inputFkId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "searchValue", required = false) String searchValue
    ) throws FinalException {
        Integer inputPkIdInt = null;
        Integer inputFkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }

        if (Util.isDefined(inputPkId)) {
            inputFkIdInt = Util.convertStringToInteger(inputFkId);
        }
        return individualService.getSubscriptionDefinition(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }


    @DeleteMapping("/deleteSubscriptionDefinition/{id}")
    FinalResponse deleteSubscriptionDefinition(@PathVariable Integer id) throws FinalException {
        return this.individualService.deleteSubscriptionDefinition(id);
    }

    @PutMapping({"/updateSubscriptionDefinition/{id}"})
    public FinalResponse updateSubscriptionDefinition(@PathVariable Integer id, @RequestBody SubscriptionDefinition subscriptionDefinition) {
        return this.individualService.updateSubscriptionDefinition(id, subscriptionDefinition);
    }

    @GetMapping({"/getSupportTicket"})
    public FinalResponse getSupportTicket(@RequestParam(value = "inputPkId", required = false) String inputPkId, @RequestParam(value = "inputFkId", required = false) String inputFkId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "25") int size, @RequestParam(value = "filterBy", required = false) String filterBy, @RequestParam(value = "searchValue", required = false) String searchValue) throws FinalException {
        Integer inputPkIdInt = null;
        if (Util.isDefined(inputPkId)) {
            inputPkIdInt = Util.convertStringToInteger(inputPkId);
        }
        return this.individualService.getSupportTicket(inputPkIdInt, inputFkId, page, size, filterBy, searchValue);
    }

    @PostMapping({"/addSupportTicket"})
    public FinalResponse addSupportTicket(@RequestBody SupportTicket supportTicket) throws FinalException {
        return this.individualService.addSupportTicket(supportTicket);
    }

    @PutMapping({"/updateSupportTicket/{id}"})
    public FinalResponse updateSupportTicket(@PathVariable Integer id, @RequestBody SupportTicket supportTicket) {
        return this.individualService.updateSupportTicket(id, supportTicket);
    }

    @PutMapping({"/resolveSupport/{id}"})
    public FinalResponse resolveSupport(@PathVariable Integer id) {
        return this.individualService.resolveSupport(id);
    }

    @DeleteMapping({"/deleteSupportTicket/{id}"})
    public FinalResponse deleteSupportTicket(@PathVariable Integer id) {
        return this.individualService.deleteSupportTicket(id);
    }

    @GetMapping("/getFavorites")
    public FinalResponse getFavorites(@RequestParam(value = "favoritesPkId", required = false) String favoritesPkId, @RequestParam(value = "userFkId", required = false) String userFkId, @RequestParam(value = "productFkId", required = false) String productFkId){
        Integer favoritesPkIdInt=null;
        Integer productFkIdInt=null;

        if(Util.isDefined(favoritesPkId)){
            favoritesPkIdInt=Util.convertStringToInteger(favoritesPkId);
        }

        if(Util.isDefined(productFkIdInt)){
            productFkIdInt=Util.convertStringToInteger(productFkId);
        }

        return individualService.getFavorites(favoritesPkIdInt,userFkId,productFkIdInt);
    }

    @PostMapping("/postFavorites")
    public FinalResponse postFavorites(@RequestBody Favorites favorites){
        return individualService.postFavorites(favorites);
    }

    @PutMapping("/updateFavorites")
    public FinalResponse updateFavorites(@RequestBody Favorites favorites){
        return individualService.updateFavorrites(favorites);
    }

    @DeleteMapping("/deleteFavorites")
        public FinalResponse updateFavorites(@RequestParam (value = "favoritePkId") Integer favoritePkId){
            return individualService.deleteFavorites(favoritePkId);
        }

    @GetMapping("/getDashBoardDetail")
    public FinalResponse getDashBoardDetail(){
        return individualService.getDashBoardDetail();
    }

    @GetMapping("/getUserWallet")
    public FinalResponse getUserWallet(
            @RequestParam (value = "userWalletPkId",required=false) Integer userWalletPkId,
            @RequestParam (value = "userFkId",required=false) String userFkId
            ){
        return individualService.getUserWallet(userWalletPkId,userFkId);
    }

    @PostMapping("/postUserWallet")
    public FinalResponse postUserWallet(@RequestBody UserWallet userWallet){
        return individualService.postUserWallet(userWallet);
    }

    @DeleteMapping("/deleteUserWallet")
    public FinalResponse deleteUserWallet(
            @RequestParam (value = "userWalletPkId",required=false) Integer userWalletPkId,
            @RequestParam (value = "userFkId",required=false) String userFkId
    ){
        return individualService.deleteUserWallet(userWalletPkId,userFkId);
    }

    @PutMapping("/putUserWallet")
    public FinalResponse putUserWallet(@RequestBody UserWallet userWallet){
        return individualService.putUserWallet(userWallet);
    }


    @GetMapping("/getUserVisit")
    public FinalResponse getUserVisit(
            @RequestParam (value="userVisitPkId", required = false) Integer userVisitPkId,
            @RequestParam (value="userFkId" , required = false) String userFkId
    ){
      return individualService.getUserVisit(userVisitPkId,userFkId);
    }

    @DeleteMapping("/deleteUserVisit")
    public FinalResponse deleteUserVisit(
            @RequestParam (value="userVisitPkId", required = false) Integer userVisitPkId
    ){
        return individualService.deleteUserVisit(userVisitPkId);
    }

    @PostMapping("/postUserVisit")
    public FinalResponse postUserVisit(@RequestBody UserVisit userVisit){
        return individualService.postUserVisit(userVisit);
    }

    @PutMapping("/putUserVisit")
    public FinalResponse putUserVisit(@RequestBody UserVisit userVisit){
        return individualService.putUserVisit(userVisit);
    }

    @GetMapping("/getUserReview")
    public FinalResponse getUserReview(
            @RequestParam (value="userReviewPkId", required = false) Integer userReviewPkId,
            @RequestParam (value="userFkId" , required = false) String userFkId,
            @RequestParam (value="productFkId", required = false) Integer productFkId
    ){
        return individualService.getUserReview(userReviewPkId,userFkId,productFkId);
    }

    @DeleteMapping("/deleteUserReview")
    public FinalResponse deleteUserReview(
            @RequestParam (value="userReviewPkId", required = false) Integer userReviewPkId
    ){
        return individualService.deleteUserReview(userReviewPkId);
    }

    @PostMapping("/postUserReview")
    public FinalResponse postUserReview(@RequestBody UserReview userReview){
        return individualService.postUserReview(userReview);
    }

    @PutMapping("/putUserReview")
    public FinalResponse putUserVisit(@RequestBody UserReview userReview){
        return individualService.putUserReview(userReview);
    }

    @GetMapping("/generateAuthBridgeToken")
    public FinalResponse generateAuthBridgeToken (){
        return individualService.generateAuthBridgeToken();
    }

    @GetMapping("/generateAuthbridgeReportDetail")
    public FinalResponse generateAuthbridgeReportDetail(@RequestParam ("requestId") String requestId){
        return individualService.generateAuthbridgeReportDetail(requestId);
    }

    @GetMapping("/generateOrderTokenResponse")
    public FinalResponse generateOrderTokenResponse(){
        return individualService.generateOrderTokenResponse();
    }

    @PostMapping("/createShiprocketOrder")
    public FinalResponse createShiprocketOrder(@RequestBody OrderRequestBody orderRequestBody){
        return individualService.createShiprocketOrder(orderRequestBody);
    }


}
