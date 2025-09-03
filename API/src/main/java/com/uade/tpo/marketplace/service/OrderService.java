package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.exceptions.InvalidOrderTransitionException;
import com.uade.tpo.marketplace.exceptions.OrderNotFoundException;

public interface OrderService {

    Page<Order> getOrders(PageRequest pageable);

    Optional<Order> getOrderById(Long orderId);

    /* 
    void cancelOrder(Long orderId, User currentUser) 
            throws InvalidOrderTransitionException, OrderNotFoundException;
    */
    
    // Paso de estados automatico
    void markAsPaid(Long orderId) 
            throws InvalidOrderTransitionException, OrderNotFoundException;   // se llama desde el módulo de pagos

    void markAsPending(Long orderId)
            throws InvalidOrderTransitionException, OrderNotFoundException; // se llama desde logística

    void markAsCanceled(Long orderId)
            throws InvalidOrderTransitionException, OrderNotFoundException; // se llama al confirmar entrega

    void markAsCompleted(Long orderId) 
            throws InvalidOrderTransitionException, OrderNotFoundException;

}
