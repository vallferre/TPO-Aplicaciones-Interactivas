package com.uade.tpo.marketplace.controllers;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.dto.FavoriteRequest;
import com.uade.tpo.marketplace.entity.dto.FavoriteResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/{userId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable Long userId,
                                            @RequestBody FavoriteRequest request) throws AccessDeniedException {
        FavoriteResponse response = favoriteService.addFavoriteProduct(userId, request.getProductName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId,
                                               @RequestBody FavoriteRequest request) throws AccessDeniedException {
        favoriteService.removeFavoriteProduct(userId, request.getProductName());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Set<Product>> getFavorites(@PathVariable Long userId) throws AccessDeniedException {
        return ResponseEntity.ok(favoriteService.getFavoriteProducts(userId));
    }
}