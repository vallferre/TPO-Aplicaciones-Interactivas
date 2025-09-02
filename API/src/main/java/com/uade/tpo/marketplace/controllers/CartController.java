package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Obtener el carrito de un usuario
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartByUser(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // Agregar producto al carrito
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestParam String productName,
            @RequestParam(defaultValue = "1") int quantity) {

        Cart updatedCart = cartService.addProductToCart(userId, productName, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    // Eliminar producto del carrito
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Cart> removeProductFromCart(
            @RequestParam Long cartId,
            @PathVariable Long userId,
            @RequestParam String productName) throws AccessDeniedException {

        Cart updatedCart = cartService.removeProductFromCart(cartId, productName, userId);
        return ResponseEntity.ok(updatedCart);
    }

    // Vaciar carrito
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

