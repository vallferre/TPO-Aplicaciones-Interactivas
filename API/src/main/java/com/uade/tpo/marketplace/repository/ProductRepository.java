package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.marketplace.entity.Product;
import java.util.List;



public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category :category")
    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.description :description")
    List<Product> findByDescription(String description);

}
