package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}