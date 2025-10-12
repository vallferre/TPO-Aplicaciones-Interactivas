package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.OrderRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Order> getOrderById(Long orderId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        User currentUser = getCurrentUser();
        // Si el usuario no es admin y no es el dueño de la orden
        if (!currentUser.getId().equals(order.getUser().getId()) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        return Optional.of(order); // ya lo trae JpaRepository
    }

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getOrdersByUser(Pageable pageable, Long userId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal(); 

        // Validar permisos
        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        // Retornar órdenes del usuario
        return orderRepository.findByUserId(userId, pageable);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal(); 
    }
    
}