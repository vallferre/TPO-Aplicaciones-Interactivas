package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.OrderRepository;

@Service
public class OrderServiceImpl {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrdersById(Long orderId){
        return orderRepository.findById(orderId);
    }

    public Order createOrder(Long count, User user){
        return orderRepository.save(new Order(count, user));
    }

}
