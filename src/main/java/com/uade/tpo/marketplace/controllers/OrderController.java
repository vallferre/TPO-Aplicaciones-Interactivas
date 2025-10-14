package com.uade.tpo.marketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.OrderResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

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
        @GetMapping("/user")
        public Page<OrderResponse> getOrdersByUser(
                @RequestHeader("Authorization") String authHeader,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "desc") String sort) throws AccessDeniedException {

            // Validar header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AccessDeniedException();
            }

            // Extraer token
            String token = authHeader.substring(7);

            // Extraer username del token
            String username = jwtService.extractUsername(token);

            // Buscar el usuario en la base
            User requester = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

            // Ordenar por la propiedad real de la entidad (id o createdAt)
            Sort sortOrder = sort.equalsIgnoreCase("desc") 
                    ? Sort.by("id").descending()  // <-- usar 'id' que es el campo real de Order
                    : Sort.by("id").ascending();

            // Llamar al service con el userId y la ordenación
            Page<Order> orders = orderService.getOrdersByUser(PageRequest.of(page, size, sortOrder), requester.getId());

            // Mapear a DTO (OrderResponse)
            return orders.map(OrderResponse::from);
        }


}
