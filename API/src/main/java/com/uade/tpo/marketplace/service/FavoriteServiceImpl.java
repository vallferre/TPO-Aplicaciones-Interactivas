package com.uade.tpo.marketplace.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addFavoriteProduct(Long userId, String productName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        user.getFavoriteProducts().add(product);
        userRepository.save(user);
    }

    @Override
    public void removeFavoriteProduct(Long userId, String productName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getFavoriteProducts().removeIf(p -> p.getName().equals(productName));
        userRepository.save(user);
    }

    @Override
    public Set<Product> getFavoriteProducts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFavoriteProducts();
    }
}