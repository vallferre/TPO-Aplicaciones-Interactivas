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

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.exceptions.InvalidOrderTransitionException;
import com.uade.tpo.marketplace.exceptions.OrderNotFoundException;
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

    /* 
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId,
                                            @AuthenticationPrincipal User currentUser) {

        try {
            orderService.cancelOrder(orderId, currentUser);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderTransitionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    */

    // Marcar como pagada 
    @PostMapping("/{orderId}/pending")
    public ResponseEntity<Void> markAsPending(@PathVariable Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        orderService.markAsPending(orderId);
        return ResponseEntity.ok().build();
    }


    // Marcar como pagada 
    @PostMapping("/{orderId}/paid")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        orderService.markAsPaid(orderId);
        return ResponseEntity.ok().build();
    }

    // Marcar como enviada
    @PostMapping("/{orderId}/completed")
    public ResponseEntity<Void> markAsCompleted(@PathVariable Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        orderService.markAsCompleted(orderId);
        return ResponseEntity.ok().build();
    }

    // Marcar como entregada
    @PostMapping("/{orderId}/canceled")
    public ResponseEntity<Void> markAsCanceled(@PathVariable Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        orderService.markAsCanceled(orderId);
        return ResponseEntity.ok().build();
    }

}