package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.entity.dto.OrderResponse;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface OrderService {

    public Page<OrderResponse> getOrders(Pageable pageable);

    public Optional<OrderResponse> getOrderById(Long orderId) throws AccessDeniedException;

    public Page<OrderResponse> getOrdersByUser(Pageable pageable, Long userId) throws AccessDeniedException;


}