package com.uade.tpo.marketplace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findByUser(@Param("userId") Long userId);
}
