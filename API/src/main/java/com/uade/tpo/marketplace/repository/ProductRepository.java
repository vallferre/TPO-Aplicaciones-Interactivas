package com.uade.tpo.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.marketplace.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar por nombre (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByName(String name);

    @Query(value = "SELECT * FROM product p WHERE p.owner_id = :ownerId", nativeQuery = true)
    List<Product> findByOwner(@Param("ownerId") Long ownerId);
    //Búsquedas básicas
    // Buscar por categoría (ManyToMany)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) = LOWER(:categoryName)")
    List<Product> findByCategory(@Param("categoryName") String categoryName);

    // O por múltiples descripciones
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) IN :descriptions")
    List<Product> findByCategoryDescriptions(@Param("descriptions") List<String> descriptionsLower);

    //Búsquedas por precio (no hace falta LOWER porque son números)
    List<Product> findByPriceGreaterThan(double price);
    List<Product> findByPriceLessThan(double price);

    //Búsquedas por stock (numérico, sin LOWER)
    List<Product> findByStockGreaterThan(int stock);

    // Buscar productos agotados (stock = 0) (sin cambios)
    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStock();

    //Búsqueda flexible por texto en descripción
    // Ya lo resolvés con IgnoreCase de Spring Data, no hace falta cambiar.
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);

    //Ordenamientos (numérico, no necesita LOWER)
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();
    List<Product> findAllByOrderByStockDesc();

    //Combinaciones personalizadas
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) = LOWER(:categoryName) AND p.price < :price")
    List<Product> findByCategoryAndPriceLessThan(@Param("categoryName") String categoryName, double price);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.description) = LOWER(:categoryName) AND p.price > :price")
    List<Product> findByCategoryAndPriceGreaterThan(@Param("categoryName") String categoryName, double price);

    // Verifica si un producto ya existe para ese owner con mismo nombre y descripción (case-insensitive)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p " +
        "WHERE p.owner.id = :ownerId AND LOWER(p.name) = LOWER(:name) AND LOWER(p.description) = LOWER(:description)")
    boolean existsByOwnerIdAndNameAndDescription(@Param("ownerId") Long ownerId,
                                                @Param("name") String name,
                                                @Param("description") String description);


}
