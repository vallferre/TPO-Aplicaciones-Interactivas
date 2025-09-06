package com.uade.tpo.marketplace.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

     @Autowired
    private OrderService orderService;


    // Solo admin
    @GetMapping
    public Page<Order> getOrders(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrders(PageRequest.of(page, size));
    }

    @GetMapping("/{orderId}")
    public Optional<Order> getOrderById(@PathVariable Long orderId) {

        return orderService.getOrderById(orderId);
    }

    // Obtener órdenes de un usuario (solo admin o el mismo usuario)
    @GetMapping("/user/{userId}")
    public Page<Order> getOrdersByUser(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByUser(PageRequest.of(page, size), userId);
    }

    @PostMapping("/{orderId}/invoice")
    public ResponseEntity<Invoice> generateInvoice(@PathVariable Long orderId) {
        try {
            Invoice invoice = orderService.generateInvoiceForOrder(orderId);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}