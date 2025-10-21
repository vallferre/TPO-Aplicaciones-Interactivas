package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.product.id = :productId")
    Double findAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId")
    int findNumberOfRatings(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.value = :value")
    int countByProductAndValue(@Param("productId") Long productId, @Param("value") int value);

    // Todos los ratings de un producto
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId")
    List<Rating> findByProductId(@Param("productId") Long productId);

    // Buscar rating de un producto por usuario (para evitar duplicados)
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.user.id = :userId")
    Rating findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);
}