package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    int countByProductPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndSellerId(String filterBy, String inputFkId);

    long countByActiveStateCodeFkId(String filterBy);

    Product findByProductPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<Product> findByActiveStateCodeFkIdAndSellerId(String filterBy, String inputFkId, Pageable pageable);

    List<Product> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    List<Product> findByActiveStateCodeFkIdAndCategoryId(String filterBy, Integer categoryId, Pageable pageable);

    int countByActiveStateCodeFkIdAndCategoryId(String activeStateCodeFkId, Integer attr0);

    @Modifying
    @Query(value = "UPDATE products SET title = :title1,description = :description1,price = :price1,location = :location1,city = :city1,zip_code = :zipCode1,state = :state1,country = :country1,category_id = :categoryId1,sub_category_id = :subcategoryId1 WHERE product_pk_id = :productPkId1", nativeQuery = true)
    void updateProduct(Integer productPkId1, String title1, String description1, BigDecimal price1, String location1, String city1, String zipCode1, String state1, String country1, int categoryId1, int subcategoryId1);
}
