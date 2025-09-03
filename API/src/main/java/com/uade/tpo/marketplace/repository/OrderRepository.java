package com.uade.tpo.marketplace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.marketplace.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    java.util.List<Order> findByUserId(Long userId);

    Optional<Order> getOrderById(Long orderId);

    //void cancelOrder(Long orderId, User currentUser);


}