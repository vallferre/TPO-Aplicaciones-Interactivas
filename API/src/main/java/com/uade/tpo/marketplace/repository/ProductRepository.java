package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);

    //Búsquedas básicas
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.description = :description")
    Optional<Product> findByDescription(String description);

    //Búsquedas por precio
    List<Product> findByPriceGreaterThan(double price);
    List<Product> findByPriceLessThan(double price);

    //Búsquedas por stock
    List<Product> findByStockGreaterThan(int stock);

    // Buscar productos agotados (stock = 0)
    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStock();

    //Búsqueda flexible por texto en descripción
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);

    //Ordenamientos
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();
    List<Product> findAllByOrderByStockDesc();

    //Combinaciones personalizadas
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price < :price")
    List<Product> findByCategoryAndPriceLessThan(Category category, double price);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price > :price")
    List<Product> findByCategoryAndPriceGreaterThan(Category category, double price);
}
