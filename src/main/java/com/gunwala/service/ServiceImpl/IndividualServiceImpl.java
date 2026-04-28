package com.gunwala.service.ServiceImpl;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.Util;
import com.gunwala.model.entitities.admin.SubscriptionDefinition;
import com.gunwala.model.entitities.enduser.SupportTicket;
import com.gunwala.model.entitities.gunwala.Category;
import com.gunwala.model.entitities.gunwala.Product;
import com.gunwala.model.entitities.gunwala.ProductImage;
import com.gunwala.repo.UserRepository;
import com.gunwala.repo.admin.SubscriptionDefinitionRepo;
import com.gunwala.repo.enduser.SupportTicketRepository;
import com.gunwala.repo.gunwala.CategoryRepository;
import com.gunwala.repo.gunwala.ProductImageRepository;
import com.gunwala.repo.gunwala.ProductRepository;
import com.gunwala.service.Service.ImageUploadService;
import com.gunwala.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndividualServiceImpl implements IndividualService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private SubscriptionDefinitionRepo subscriptionDefinitionRepo;
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse addProduct(Product product) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        //effective date cannot be greater than present date
        product.setEffectiveDateTime(vLastModifiedDateTime);
        if (Util.compareDate(product.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(product);
        productRepository.save(product);
       for(ProductImage productImage: product.getProductImageList()) {
           productImage.setProductFkId(product.getProductPkId());
           finalResponse = addProductImage(productImage);
       }

        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getProduct(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<Product> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<Product> productList = populateProductView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateUserViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(productList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateUserViewCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                count = productRepository.countByProductPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            } else if (Util.isDefined(inputFkId)) {
                count = productRepository.countByActiveStateCodeFkIdAndSellerId(filterBy, inputFkId);
            } else {
                long count1 = productRepository.countByActiveStateCodeFkId(filterBy);
                count = Math.toIntExact(count1);
            }
        }  else{
            count = Math.toIntExact(productRepository.count());
        }

        return count;
    }

    private List<Product> populateProductView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {

        List<Product> productList = new ArrayList<>();
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                Product product = productRepository.findByProductPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
                productList.add(product);
            } else if (Util.isDefined(inputFkId)) {
                productList = productRepository.findByActiveStateCodeFkIdAndSellerId(filterBy, inputFkId, pageable);
            } else {
                productList = productRepository.findByActiveStateCodeFkId(filterBy, pageable);
            }
        }
        else{
            productList = productRepository.findAll( );
        }
        productList.stream().map((product -> {
            List<ProductImage> productImageList=productImageRepository.findByActiveStateCodeFkIdAndProductFkId(filterBy,product.getProductPkId(),null);
            for(ProductImage productImage:productImageList) {
                if(Util.isDefined(productImage.getProductImageId())) {
                    String presignedUrl = imageUploadService.generatePresignedUrl(productImage.getProductImageId());
                    productImage.setProfileImageUrl(presignedUrl);
                }
            }
            product.setProductImageList(productImageList);
            return product;
        })).collect(Collectors.toList());
        return productList;
    }

    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse updateProduct(Integer productPkId, Product product) {
        FinalResponse finalResponse = new FinalResponse();
        this.productRepository.findById(productPkId)
                .map(existing -> {
                    existing.setPrice(product.getPrice());
                    return (Product)this.productRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Product not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse deleteProduct(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.productRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse addCategory(Category category) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        //effective date cannot be greater than present date
        category.setEffectiveDateTime(vLastModifiedDateTime);
        if (Util.compareDate(category.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(category);
        categoryRepository.save(category);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getCategory(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<Category> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<Category> categoryList = populateCategoryView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateCategoryViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(categoryList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateCategoryViewCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                count = categoryRepository.countByCategoryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            }
            else {
                long count1 = categoryRepository.countByActiveStateCodeFkId(filterBy);
                count = Math.toIntExact(count1);
            }
        }  else{
            count = Math.toIntExact(categoryRepository.count());
        }

        return count;
    }

    private List<Category> populateCategoryView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {

        List<Category> categoryList = new ArrayList<>();
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                Category category = categoryRepository.findByCategoryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
                categoryList.add(category);
            }
            else {
                categoryList = categoryRepository.findByActiveStateCodeFkId(filterBy, pageable);
            }
        }
        else{
            categoryList = categoryRepository.findAll( );
        }

        return categoryList;
    }

    @Override
    public FinalResponse deleteCategory(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.categoryRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateCategory(Integer categoryPkId, Category category) {
        FinalResponse finalResponse = new FinalResponse();
        this.categoryRepository.findById(categoryPkId)
                .map(existing -> {
                    existing.setCategoryName(category.getCategoryName());
                    return (Category)this.categoryRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Category not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse addProductImage(ProductImage productImage) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        //effective date cannot be greater than present date
        productImage.setEffectiveDateTime(vLastModifiedDateTime);
        if (Util.compareDate(productImage.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(productImage);
        productImageRepository.save(productImage);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getProductImage(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<ProductImage> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<ProductImage> productImageList = populateProductImageView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateProductImageViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(productImageList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateProductImageViewCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                count = productImageRepository.countByProductImagePkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            } else if (Util.isDefined(inputFkId)) {
                count = productImageRepository.countByActiveStateCodeFkIdAndProductFkId(filterBy, inputFkId);
            } else {
                long count1 = productImageRepository.countByActiveStateCodeFkId(filterBy);
                count = Math.toIntExact(count1);
            }
        }  else{
            count = Math.toIntExact(productImageRepository.count());
        }

        return count;
    }

    private List<ProductImage> populateProductImageView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {

        List<ProductImage> productImageList = new ArrayList<>();
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                ProductImage productImage = productImageRepository.findByProductImagePkIdAndActiveStateCodeFkId(inputPkId, filterBy);
                productImageList.add(productImage);
            } else if (Util.isDefined(inputFkId)) {
                productImageList = productImageRepository.findByActiveStateCodeFkIdAndProductFkId(filterBy, inputFkId, pageable);
            } else {
                productImageList = productImageRepository.findByActiveStateCodeFkId(filterBy, pageable);
            }
        }
        else{
            productImageList = productImageRepository.findAll( );
        }

        return productImageList;
    }

    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse updateProductImage(Integer productPkId, ProductImage productImage) {
        FinalResponse finalResponse = new FinalResponse();
        this.productImageRepository.findById(productPkId)
                .map(existing -> {
                    existing.setProductFkId(productImage.getProductFkId());
                    return (ProductImage)this.productImageRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("ProductImage not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse deleteProductImage(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.productImageRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse addSubscriptionDefinition(SubscriptionDefinition subscriptionDefinition) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        //effective date cannot be greater than present date
        subscriptionDefinition.setEffectiveDateTime(vLastModifiedDateTime);
        if (Util.compareDate(subscriptionDefinition.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(subscriptionDefinition);
        subscriptionDefinitionRepo.save(subscriptionDefinition);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getSubscriptionDefinition(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<SubscriptionDefinition> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<SubscriptionDefinition> subscriptionDefinitionList = populateSubscriptionDefinitionView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateSubscriptionDefinitionViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(subscriptionDefinitionList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateSubscriptionDefinitionViewCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                count = subscriptionDefinitionRepo.countBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            } else if (Util.isDefined(inputFkId)) {
                count = subscriptionDefinitionRepo.countByActiveStateCodeFkIdAndSubscriptionCode(filterBy, inputFkId);
            } else {
                long count1 = subscriptionDefinitionRepo.countByActiveStateCodeFkId(filterBy);
                count = Math.toIntExact(count1);
            }
        }  else{
            count = Math.toIntExact(subscriptionDefinitionRepo.count());
        }

        return count;
    }

    private List<SubscriptionDefinition> populateSubscriptionDefinitionView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {

        List<SubscriptionDefinition> subscriptionDefinitionList = new ArrayList<>();
        if(Util.isDefined(filterBy)) {
            if (Util.isDefined(inputPkId)) {
                SubscriptionDefinition subscriptionDefinition = subscriptionDefinitionRepo.findBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
                subscriptionDefinitionList.add(subscriptionDefinition);
            } else if (Util.isDefined(inputFkId)) {
                subscriptionDefinitionList = subscriptionDefinitionRepo.findByActiveStateCodeFkIdAndSubscriptionCode(filterBy, inputFkId, pageable);
            } else {
                subscriptionDefinitionList = subscriptionDefinitionRepo.findByActiveStateCodeFkId(filterBy, pageable);
            }
        }
        else{
            subscriptionDefinitionList = subscriptionDefinitionRepo.findAll( );
        }

        return subscriptionDefinitionList;
    }

    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse updateSubscriptionDefinition(Integer productPkId, SubscriptionDefinition subscriptionDefinition) {
        FinalResponse finalResponse = new FinalResponse();
        this.subscriptionDefinitionRepo.findById(productPkId)
                .map(existing -> {
                    existing.setSubscriptionAmount(subscriptionDefinition.getSubscriptionAmount());
                    existing.setSubscriptionCode(subscriptionDefinition.getSubscriptionCode());
                    return (SubscriptionDefinition)this.subscriptionDefinitionRepo.save(existing);
                }).orElseThrow(() -> new RuntimeException("SubscriptionDefinition not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse deleteSubscriptionDefinition(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.subscriptionDefinitionRepo.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Override
    public FinalResponse getSupportTicket(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<SupportTicket> finalResponse = new FinalResponse();
        Pageable pageable = Util.getPageable(size, page);
        List<SupportTicket> supportTicketList = populateSupportTicketView(inputPkId, inputFkId, filterBy, searchValue, pageable);
        Long count = populateSupportTicketCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(supportTicketList);
        finalResponse.setCount(Math.toIntExact(count));
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    private Long populateSupportTicketCount(Integer inputPkId, String inputFkId, String filterBy) {
        Long count = Long.valueOf(0L);
        if (Util.isDefined(inputPkId)) {
            count = this.supportTicketRepository.countBySupportTicketPkIdAndActiveStateCodeFkId(inputPkId,"ACTIVE");
        }
        else if (Util.isDefined(inputFkId)) {
            count = this.supportTicketRepository.countByUserNodeIdAndActiveStateCodeFkId(inputFkId,"ACTIVE");
        } else {

            count = Long.valueOf(this.supportTicketRepository.count());
        }

        return count;
    }

    private List<SupportTicket> populateSupportTicketView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<SupportTicket> supportTicketList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            SupportTicket supportTicket = this.supportTicketRepository.findBySupportTicketPkIdAndActiveStateCodeFkId(inputPkId,"ACTIVE");
            supportTicketList.add(supportTicket);
        }
        else if (Util.isDefined(inputFkId)) {
            supportTicketList = this.supportTicketRepository.findByUserNodeIdAndActiveStateCodeFkId(inputFkId,"ACTIVE");
        } else {

            supportTicketList = this.supportTicketRepository.findAll();
        }
        supportTicketList.stream().map(supportTicket -> {
            String userName = this.userRepository.fetchUserNameBasedOnNodeId(supportTicket.getUserNodeId(),"ACTIVE");
            supportTicket.setUserName(userName);
            return supportTicket;
        }).collect(Collectors.toList());
        return supportTicketList;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse addSupportTicket(SupportTicket supportTicket) {
        FinalResponse finalResponse = new FinalResponse();
        supportTicket.setStatus("IN_PROGRESS");
        this.supportTicketRepository.save(supportTicket);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse updateSupportTicket(Integer id, SupportTicket supportTicket) {
        FinalResponse finalResponse = new FinalResponse();
        this.supportTicketRepository.findById(id)
                .map(existing -> {
                    existing.setCategory(supportTicket.getCategory());
                    existing.setPriority(supportTicket.getPriority());
                    existing.setUserNodeId(supportTicket.getUserNodeId());
                    existing.setMessage(supportTicket.getMessage());
                    existing.setStatus(supportTicket.getStatus());
                    return (SupportTicket)this.supportTicketRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Support ticket  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse resolveSupport(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.supportTicketRepository.findById(id)
                .map(existing -> {
                    existing.setStatus("RESOLVED");
                    return (SupportTicket)this.supportTicketRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Support ticket  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Transactional(rollbackFor = {Exception.class})
    public FinalResponse deleteSupportTicket(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        this.supportTicketRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



}
