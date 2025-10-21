package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Obtener todos los favoritos de un usuario
    List<Favorite> findByUserId(Long userId);

    // Verificar si un producto ya está marcado como favorito por un usuario
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    // Eliminar un favorito específico
    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT f.user.id FROM Favorite f WHERE f.productId = :productId")
    List<Long> findUserIdsByProductId(@Param("productId") Long productId);
}
