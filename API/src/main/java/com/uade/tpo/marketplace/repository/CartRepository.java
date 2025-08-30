package com.uade.tpo.marketplace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>{
    @Query("SELECT c FROM Cart c WHERE c.user = :user")
    Optional<Cart> findByUser(@Param("user") User user);
}
