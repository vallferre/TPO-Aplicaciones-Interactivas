package com.uade.tpo.marketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.InvoiceResponse;
import com.uade.tpo.marketplace.entity.dto.OrderResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Obtener todas las órdenes (solo admin)
    @GetMapping
    public Page<OrderResponse> getOrders(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        if (!currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        Page<Order> orders = orderService.getOrders(PageRequest.of(page, size));
        return orders.map(OrderResponse::new);
    }

    // Obtener una orden por ID (solo admin o dueño)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId,
                                                      @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validación de permisos
        if (!currentUser.getId().equals(order.getUser().getId()) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        OrderResponse response = new OrderResponse(order);
        return ResponseEntity.ok(response);
    }

    // Obtener órdenes de un usuario (solo admin o el mismo usuario)
    @GetMapping("/user/{userId}")
    public Page<OrderResponse> getOrdersByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        Page<Order> orders = orderService.getOrdersByUser(PageRequest.of(page, size), userId);
        return orders.map(OrderResponse::new);
    }

    // Generar invoice a partir de una orden (solo comprador puede generar)
    @PostMapping("/{orderId}/invoice")
    public ResponseEntity<InvoiceResponse> generateInvoice(@PathVariable Long orderId) {
        try {
            // Ahora el service devuelve un DTO, no la entidad JPA
            InvoiceResponse invoiceResponse = orderService.generateInvoiceForOrder(orderId);
            return ResponseEntity.ok(invoiceResponse);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
