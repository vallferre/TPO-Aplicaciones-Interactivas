package com.uade.tpo.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Buscar órdenes por el id del usuario (paginadas)
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT COUNT(oi) > 0 
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.user.id = :userId AND oi.product.id = :productId
    """)
    boolean existsByUserIdAndProductId(@Param("userId") Long userId,
                                    @Param("productId") Long productId);


}