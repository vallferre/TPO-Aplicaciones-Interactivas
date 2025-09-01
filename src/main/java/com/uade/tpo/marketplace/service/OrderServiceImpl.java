package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrdersById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Order createOrder(Long count, User user) {
        Order order = new Order();
        order.setCount(count);
        order.setUser(user);
        return orderRepository.save(order);
    }
}