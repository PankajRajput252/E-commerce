package com.gunwala.repo.gunwala;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.entitities.gunwala.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.lang.annotation.Native;
import java.time.LocalDateTime;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorites,Integer> {

    List<Favorites> findByFavoritesPkId(Integer favoritesPkId);

    List<Favorites> findByUserFkId(String userFkId);

    List<Favorites> findByProductFkId(Integer productFkId);

    @Modifying
    @Query(value = "UPDATE favorites SET USER_FK_ID=:userFkId, PRODUCT_FK_ID=:productFkId, CREATED_AT=:createdAt WHERE FAVORITES_PK_ID=:favoritePkId", nativeQuery = true)
    void updateFavorite(Integer favoritePkId,String userFkId, Integer productFkId, LocalDateTime createdAt);


}
