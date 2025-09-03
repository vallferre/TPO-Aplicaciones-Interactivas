package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.Order.OrderStatus;
import com.uade.tpo.marketplace.exceptions.InvalidOrderTransitionException;
import com.uade.tpo.marketplace.exceptions.OrderNotFoundException;
import com.uade.tpo.marketplace.repository.OrderRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Page<Order> getOrders(PageRequest pageable) {
        return orderRepository.findAll(pageable);
    }

    /* 
    @Override
    public void cancelOrder(Long orderId, User currentUser)
            throws InvalidOrderTransitionException, OrderNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new IllegalStateException("No se puede cancelar una orden ya pagada");
        }

        order.setStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(order);
    }
    */

    @Transactional
    @Override
    public void markAsPaid(Long orderId) 
            throws InvalidOrderTransitionException, OrderNotFoundException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderTransitionException("Solo se puede marcar como pagada una orden pendiente");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }


    @Transactional
    @Override
    public void markAsCompleted(Long orderId) 
            throws InvalidOrderTransitionException, OrderNotFoundException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderTransitionException("Solo se puede completar una orden pagada");
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Override
    public void markAsPending(Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markAsPending'");
    }

    @Override
    public void markAsCanceled(Long orderId) throws InvalidOrderTransitionException, OrderNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markAsCanceled'");
    }

    
}