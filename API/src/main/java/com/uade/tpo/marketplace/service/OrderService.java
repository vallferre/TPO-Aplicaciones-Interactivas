package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.marketplace.entity.Order;

public interface OrderService {

    Page<Order> getOrders(PageRequest pageable);

    Optional<Order> getOrderById(Long orderId);

}
