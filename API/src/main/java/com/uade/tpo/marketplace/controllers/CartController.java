package com.uade.tpo.marketplace.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.CartRequest;
import com.uade.tpo.marketplace.entity.dto.CartResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;
import com.uade.tpo.marketplace.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Obtener el carrito de un usuario
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUser(@PathVariable Long userId, @AuthenticationPrincipal User requester) throws AccessDeniedException {

        List<CartItem> cartItems = cartService.getCartItems(userId, requester);
        CartResponse response = new CartResponse(cartItems, cartService.getCartTotal(userId));
        return ResponseEntity.ok(response);
    }

    // Agregar producto al carrito
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestBody CartRequest request,
            @RequestParam(defaultValue = "1") int quantity,
            @AuthenticationPrincipal User requester) throws AccessDeniedException {

        Cart updatedCart = cartService.addProductToCart(userId, request.getProductName(), quantity, requester);
        return ResponseEntity.ok(updatedCart);
    }

    // Eliminar producto del carrito
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long userId,
            @RequestBody CartRequest request) throws AccessDeniedException {

        Cart updatedCart = cartService.removeProductFromCart(request.getProductName(), userId);
        return ResponseEntity.ok(updatedCart);
    }

    // Vaciar carrito
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) throws AccessDeniedException {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Order> checkout(
            @PathVariable Long userId  // 
    ) throws InsufficientStockException {
        try {
            Order order = cartService.checkout(userId);
            return ResponseEntity.ok(order);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(null); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

