package com.uade.tpo.marketplace.controllers;

import java.util.List;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.service.OrderService;
import com.uade.tpo.marketplace.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    // Obtener todas las órdenes
    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    // Obtener una orden por ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrdersById(@PathVariable Long orderId) {
        return orderService.getOrdersById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear una orden
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam Long count,
            @RequestParam Long userId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = orderService.createOrder(count, user);
        return ResponseEntity.ok(order);
    }
}