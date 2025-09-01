package com.uade.tpo.marketplace.service;

import java.util.Optional;
import java.util.Set;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;

public interface UserService {
    public Optional<User> getUserById(Long categoryId);
    public Optional<User> getUserByEmail(String email);
    public Optional<User> getUserByUsername(String username);
    public User createUser(String email, String name, String surname, String username, String password);
    public User updateUser(User user);
    public Boolean deleteUser(Long id);
    public User addFavorite(Long userId, String productName);
    public User removeFavorite(Long userId, String productName);
    public Set<Product> getFavorites(Long userId);
}
