package com.uade.tpo.marketplace.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addFavoriteProduct(Long userId, String productName) throws AccessDeniedException{
        User currentUser = getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (currentUser.getFavoriteProducts().contains(product)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        currentUser.getFavoriteProducts().add(product);
        userRepository.save(currentUser);
    }

    @Override
    public void removeFavoriteProduct(Long userId, String productName) throws AccessDeniedException {
        User currentUser = getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        boolean removed = currentUser.getFavoriteProducts().removeIf(p -> p.getName().equals(productName));
        if (!removed) {
            throw new RuntimeException("El producto no estaba en favoritos");
        }

        currentUser.getFavoriteProducts().removeIf(p -> p.getName().equals(productName));
        userRepository.save(currentUser);
    }

    @Override
    public Set<Product> getFavoriteProducts(Long userId) throws AccessDeniedException {
        User currentUser = getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        return currentUser.getFavoriteProducts();
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}