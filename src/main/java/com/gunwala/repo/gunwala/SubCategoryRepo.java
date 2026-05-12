package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepo extends JpaRepository<SubCategory,Integer> {
    public List<SubCategory> findBySubCategoryPkId(Integer integerPkId);

    List<SubCategory> findByCategoryFkId(Integer integerFkId);

    void deleteByCategoryFkId(int i);

    @Modifying
    @Query(value = "UPDATE sub_category SET CATEGORY_FK_ID=:categoryFkId,SUB_CATEGORY_NAME=:subCategoryName WHERE SUB_CATEGORY_PK_ID=:subCategoryPkId",nativeQuery = true)
    void putSubCategory(Integer subCategoryPkId, Integer categoryFkId, String subCategoryName);
}
