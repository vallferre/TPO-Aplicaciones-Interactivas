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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.CartRequest;
import com.uade.tpo.marketplace.entity.dto.CartResponse;
import com.uade.tpo.marketplace.entity.dto.OrderResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.EmptyCartException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    // Obtener el carrito de un usuario
    @GetMapping()
    public ResponseEntity<CartResponse> getCartByUser(@RequestHeader("Authorization") String authHeader) {
        try {

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

            Cart cart = cartService.get(requester.getId());
            List<CartItem> cartItems = cart.getItems();
            CartResponse response = new CartResponse(cart, cartItems);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Agregar producto al carrito
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CartRequest request) {

        try {

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

            Cart updatedCart = cartService.addProductToCart(requester.getId(), request.getProductId(), request.getQuantity());
            CartResponse response = new CartResponse(updatedCart, updatedCart.getItems());
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Eliminar producto del carrito
    @DeleteMapping("/remove/{number}")
    public ResponseEntity<CartResponse> removeProductFromCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CartRequest request,
            @RequestParam int number) throws ProductNotFoundException {

        try {

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

            Cart updatedCart = cartService.removeProductFromCart(request.getProductId(), requester.getId(), number);
            CartResponse response = new CartResponse(updatedCart, updatedCart.getItems());
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Vaciar carrito
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String authHeader) {
        try {

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

            cartService.clearCart(requester.getId());
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Checkout
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader("Authorization") String authHeader) {
        try {

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

            Order order = cartService.checkout(requester.getId());
            OrderResponse response = new OrderResponse(order); // <-- construir DTO
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EmptyCartException | InsufficientStockException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}