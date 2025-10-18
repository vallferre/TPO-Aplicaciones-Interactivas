package com.uade.tpo.marketplace.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.Favorite;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.FavoriteRequest;
import com.uade.tpo.marketplace.entity.dto.FavoriteResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody FavoriteRequest request) throws AccessDeniedException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));

        FavoriteResponse response = favoriteService.addFavoriteProduct(requester.getId(), request.getProductId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody FavoriteRequest request) throws AccessDeniedException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));

        favoriteService.removeFavoriteProduct(requester.getId(), request.getProductId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@RequestHeader("Authorization") String authHeader) throws AccessDeniedException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));


        return ResponseEntity.ok(favoriteService.getFavoriteProducts(requester.getId()));
    }
}