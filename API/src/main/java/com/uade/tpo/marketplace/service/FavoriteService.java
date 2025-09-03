package com.uade.tpo.marketplace.service;

import java.util.Set;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface FavoriteService {
    void addFavoriteProduct(Long userId, String productName) throws AccessDeniedException;
    void removeFavoriteProduct(Long userId, String productName) throws AccessDeniedException;
    Set<Product> getFavoriteProducts(Long userId) throws AccessDeniedException;
}
