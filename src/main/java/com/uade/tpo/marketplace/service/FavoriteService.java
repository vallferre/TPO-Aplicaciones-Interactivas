package com.uade.tpo.marketplace.service;

import java.util.Set;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.dto.FavoriteResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface FavoriteService {
    FavoriteResponse addFavoriteProduct(Long userId, long  productId) throws AccessDeniedException;
    void removeFavoriteProduct(Long userId, long  productId) throws AccessDeniedException;
    Set<Product> getFavoriteProducts(Long userId) throws AccessDeniedException;
}
