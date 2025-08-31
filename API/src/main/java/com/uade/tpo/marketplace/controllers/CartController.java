package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
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
    @GetMapping("/{email}")
    public ResponseEntity<List<CartItem>> getCartByUser(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // Agregar producto al carrito
    @PostMapping("/{email}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        Cart updatedCart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    // Eliminar producto del carrito
    @DeleteMapping("/{email}/remove")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long userId,
            @RequestParam Long productId) {

        Cart updatedCart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    // Vaciar carrito
    @DeleteMapping("/{email}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
