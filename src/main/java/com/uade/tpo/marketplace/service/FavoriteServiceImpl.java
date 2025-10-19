package com.uade.tpo.marketplace.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Favorite;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.FavoriteResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.FavoriteRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public FavoriteResponse addFavoriteProduct(Long userId, Long productId) throws AccessDeniedException {
        User currentUser = getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (currentUser.getId().equals(product.getOwner().getId())) {
            throw new AccessDeniedException("No puedes agregar su propio producto a favoritos."); // No puede marcar su propio producto
        }

        // Verificar si ya existe el favorito
        boolean alreadyExists = favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent();
        if (alreadyExists) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        // Guardar el favorito
        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .productId(productId)
                .build();
        favoriteRepository.save(favorite);

        return new FavoriteResponse(currentUser.getId(), productId, "Producto agregado a favoritos");
    }

    @Override
    public void removeFavoriteProduct(Long userId, Long productId) throws AccessDeniedException {
        User currentUser = getAuthenticatedUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("El producto no estaba en favoritos"));

        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getFavoriteProducts(Long userId) throws AccessDeniedException {
        User currentUser = getAuthenticatedUser();

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUser.getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        return favoriteRepository.findByUserId(userId);
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }
}
