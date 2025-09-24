package com.uade.tpo.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.dto.ProductResponse;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================
    // Búsquedas básicas / owner
    // =========================

    // Buscar por nombre (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByName(String name);

    @Query(value = "SELECT * FROM product p WHERE p.owner_id = :ownerId", nativeQuery = true)
    List<Product> findByOwner(@Param("ownerId") Long ownerId);

    // =========================
    // Categorías
    // =========================

    // Buscar por categoría (ManyToMany)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) = LOWER(:categoryName)")
    List<Product> findByCategory(@Param("categoryName") String categoryName);

    // O por múltiples descripciones (dejado tal cual lo tenías)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) IN :descriptions")
    List<ProductResponse> findByCategoryDescriptions(@Param("descriptions") List<String> descriptionsLower);

    // =========================
    // Stock
    // =========================
    Page<Product> findByStockGreaterThan(int stock, PageRequest pageable);

    // Productos agotados (stock = 0)
    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStock();

    // Solo devuelve productos con stock > 0, ordenados desc
    List<Product> findByStockGreaterThanOrderByStockDesc(int stock);

    // =========================
    // Texto libre en descripción
    // =========================
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);

    // =========================
    // >>> PRECIO FINAL <<<
    // TODAS las búsquedas/ordenamientos por precio usan finalPrice
    // =========================

    // Búsquedas por precio final
    List<Product> findByFinalPriceGreaterThan(double finalPrice);
    List<Product> findByFinalPriceLessThan(double finalPrice);

    // Ordenamientos por precio final
    List<Product> findAllByOrderByFinalPriceAsc();
    List<Product> findAllByOrderByFinalPriceDesc();

    // Combinaciones con categoría y precio final
    @Query("SELECT p FROM Product p JOIN p.categories c " +
           "WHERE LOWER(c.description) = LOWER(:categoryName) AND p.finalPrice < :price")
    List<Product> findByCategoryAndFinalPriceLessThan(@Param("categoryName") String categoryName,
                                                      @Param("price") double price);

    @Query("SELECT p FROM Product p JOIN p.categories c " +
           "WHERE LOWER(c.description) = LOWER(:categoryName) AND p.finalPrice > :price")
    List<Product> findByCategoryAndFinalPriceGreaterThan(@Param("categoryName") String categoryName,
                                                         @Param("price") double price);


    // =========================
    // Duplicado (name+description) por owner
    // =========================
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p " +
        "WHERE p.owner.id = :ownerId AND LOWER(p.name) = LOWER(:name) AND LOWER(p.description) = LOWER(:description)")
    boolean existsByOwnerIdAndNameAndDescription(@Param("ownerId") Long ownerId,
                                                 @Param("name") String name,
                                                 @Param("description") String description);
}
