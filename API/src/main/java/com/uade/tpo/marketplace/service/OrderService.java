package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;

public interface OrderService {

    public Page<Order> getOrders(Pageable pageable);

    public Optional<Order> getOrderById(Long orderId);

    public Page<Order> getOrdersByUser(Pageable pageable, Long userId);

    public Invoice generateInvoiceForOrder(Long orderId);

}
