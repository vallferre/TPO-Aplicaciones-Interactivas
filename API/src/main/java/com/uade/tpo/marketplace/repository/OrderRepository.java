package com.uade.tpo.marketplace.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Buscar órdenes por el id del usuario
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);

    // Buscar orden por id
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Optional<Order> getOrderById(@Param("orderId") Long orderId);

    // (Ejemplo extra) Buscar todas las órdenes de un usuario con estado CANCELADA
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 'CANCELADA'")
    List<Order> findCancelledOrdersByUser(@Param("userId") Long userId);

    //void cancelOrder(Long orderId, User currentUser);


}