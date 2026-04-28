package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    int countByProductPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    int countByActiveStateCodeFkIdAndSellerId(String filterBy, String inputFkId);

    long countByActiveStateCodeFkId(String filterBy);

    Product findByProductPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<Product> findByActiveStateCodeFkIdAndSellerId(String filterBy, String inputFkId, Pageable pageable);

    List<Product> findByActiveStateCodeFkId(String filterBy, Pageable pageable);
}
