package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.ProductImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository  extends JpaRepository<ProductImage,Integer> {
    ProductImage findByProductImagePkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<ProductImage> findByActiveStateCodeFkIdAndProductFkId(String filterBy, Integer inputFkId, Pageable pageable);

    List<ProductImage> findByActiveStateCodeFkId(String filterBy, Pageable pageable);

    int countByProductImagePkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndProductFkId(String filterBy, Integer inputFkId);

    long countByActiveStateCodeFkId(String filterBy);
}
