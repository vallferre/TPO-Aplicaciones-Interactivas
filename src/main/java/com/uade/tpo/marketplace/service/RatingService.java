package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.entity.Rating;
import com.uade.tpo.marketplace.entity.dto.RatingResponse;

public interface RatingService {

    // Obtener promedio de ratings de un producto
    double getAverageRatingForProduct(Long productId);

    // Obtener cantidad total de ratings de un producto
    int getNumberOfRatingsForProduct(Long productId);

    // Contar ratings de un producto por valor (1 a 5)
    int countRatingsByValueForProduct(Long productId, int value);

    // Agregar o actualizar un rating de un usuario para un producto, con comentario opcional
    Rating addRatingToProduct(Long productId, Long userId, int value, String comment);

    List<RatingResponse> getRatingsForProduct(Long productId);

}