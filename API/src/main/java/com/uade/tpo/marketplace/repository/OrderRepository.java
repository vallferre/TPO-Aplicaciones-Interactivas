package com.uade.tpo.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.marketplace.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Buscar órdenes por el id del usuario (paginadas)
    Page<Order> findByUserId(Long userId, Pageable pageable);

}