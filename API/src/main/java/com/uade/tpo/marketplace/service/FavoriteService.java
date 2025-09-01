package com.uade.tpo.marketplace.service;

import java.util.Set;

import com.uade.tpo.marketplace.entity.Product;

public interface FavoriteService {
    void addFavoriteProduct(Long userId, String productName);
    void removeFavoriteProduct(Long userId, String productName);
    Set<Product> getFavoriteProducts(Long userId);
}
