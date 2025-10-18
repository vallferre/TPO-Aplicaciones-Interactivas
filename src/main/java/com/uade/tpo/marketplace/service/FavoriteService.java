package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.entity.Favorite;
import com.uade.tpo.marketplace.entity.dto.FavoriteResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface FavoriteService {
    public FavoriteResponse addFavoriteProduct(Long userId, Long productId) throws AccessDeniedException;
    public void removeFavoriteProduct(Long userId, Long productId) throws AccessDeniedException;
    public List<Favorite> getFavoriteProducts(Long userId) throws AccessDeniedException;
}
