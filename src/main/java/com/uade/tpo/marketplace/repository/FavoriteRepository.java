package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
