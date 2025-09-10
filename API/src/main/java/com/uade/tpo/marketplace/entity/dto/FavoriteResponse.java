package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.uade.tpo.marketplace.entity.User;

import lombok.Data;

@Data
public class FavoriteResponse {
    private String username;
    private List<ProductResponse> favoriteProducts;

    public FavoriteResponse(User user){
        this.username = user.getUsername();
        this.favoriteProducts = user.getFavoriteProducts()
                                    .stream()
                                    .map(ProductResponse::from)
                                    .collect(Collectors.toList());
    }
}
