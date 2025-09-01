package com.uade.tpo.marketplace.controllers;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.FavoriteRequest;
import com.uade.tpo.marketplace.entity.dto.UserRequest;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;
import com.uade.tpo.marketplace.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/email/{userEmail}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable("userEmail") String userEmail) {
        Optional<User> result = userService.getUserByEmail(userEmail);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());  // devuelve el usuario directamente
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found")); // mensaje de error si no existe
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
        Optional<User> result = userService.getUserById(userId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());  // devuelve el usuario directamente
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found")); // mensaje de error si no existe
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok(Map.of("message", "User successfully deleted"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }


    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) throws UserDuplicateException {
        User result = userService.createUser(
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getSurname(),
                userRequest.getUsername(),
                userRequest.getPassword()
        );
        return ResponseEntity.created(URI.create("/users/" + result.getId()))
                .body(Map.of("message", "User successfully created", "user", result));
    }

    @PostMapping("/{userId}/favorites")
    public ResponseEntity<Object> addFavorite(
        @PathVariable Long userId, 
        @RequestBody FavoriteRequest request) throws ProductNotFoundException {
        User updatedUser = userService.addFavorite(userId, request.getProductName());
        return ResponseEntity.ok(Map.of("message", "Product added to favorites", "favorites", updatedUser.getFavoriteProducts()));
    }

    @DeleteMapping("/{userId}/favorites")
    public ResponseEntity<Object> removeFavorite(
        @PathVariable Long userId, 
        @RequestParam String productName) throws ProductNotFoundException {
        User updatedUser = userService.removeFavorite(userId, productName);
        return ResponseEntity.ok(Map.of("message", "Product removed from favorites", "favorites", updatedUser.getFavoriteProducts()));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Object> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFavorites(userId));
    }

}
