package com.uade.tpo.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);

    //Búsquedas básicas
    // Buscar por categoría (ManyToMany)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category")
    List<Product> findByCategory(Category category);

    // O por múltiples descripciones
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.description IN :descriptions")
    List<Product> findByCategoryDescriptions(List<String> description);

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
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category AND p.price < :price")
    List<Product> findByCategoryAndPriceLessThan(Category category, double price);

    
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category AND p.price > :price")
    List<Product> findByCategoryAndPriceGreaterThan(Category category, double price);

    // Verifica si un producto ya existe para ese owner con mismo nombre y descripción
    boolean existsByOwnerIdAndNameAndDescription(Long ownerId, String name, String description);

}
