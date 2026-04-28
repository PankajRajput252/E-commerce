package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    int countByCategoryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    long countByActiveStateCodeFkId(String filterBy);

    Category findByCategoryPkIdAndActiveStateCodeFkId(Integer inputPkId, String filterBy);

    List<Category> findByActiveStateCodeFkId(String filterBy, Pageable pageable);
}
