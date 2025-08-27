package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;

public interface OrderService {

    public  list<Order> getOrders();

    public Optional<Order> getOrdersById(Long orderId);

    public Order createOrder(Long count, User user);

}
