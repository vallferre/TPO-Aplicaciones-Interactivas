package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.ProductImage;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private int stock;
    private double price;            // precio base
    private Double discountPercentage;
    private Double finalPrice;       // precio con descuento
    private String ownerName;
    private List<String> categories;
    private List<Long> imageIds;     // <<--- reemplaza URLs
    // Nuevo bloque de información de ratings
    private Double averageRating;  // Promedio general (ej: 4.2)
    private Map<Integer, Long> ratingDistribution; // cantidad por estrella
    private List<RatingResponse> ratings; // cada opinión individual

    public ProductResponse(Long id,
                           String name,
                           String description,
                           int stock,
                           double price,
                           Double discountPercentage,
                           Double finalPrice,
                           String ownerName,
                           List<String> categories,
                           List<Long> imageIds,
                           List<RatingResponse> ratings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.finalPrice = finalPrice;
        this.ownerName = ownerName;
        this.categories = categories;
        this.imageIds = imageIds;
        this.ratings = ratings;

        // Calcular promedio y distribución
        if (ratings != null && !ratings.isEmpty()) {
            this.averageRating = ratings.stream()
                    .mapToInt(RatingResponse::getValue)
                    .average()
                    .orElse(0.0);

            this.ratingDistribution = ratings.stream()
                    .collect(Collectors.groupingBy(RatingResponse::getValue, Collectors.counting()));

            // Asegurar que existan todas las estrellas del 1 al 5 aunque tengan 0
            for (int i = 1; i <= 5; i++) {
                this.ratingDistribution.putIfAbsent(i, 0L);
            }

        } else {
            this.averageRating = 0.0;
            // Generar mapa con 5 estrellas (todas 0)
            this.ratingDistribution = Map.of(
                    1, 0L,
                    2, 0L,
                    3, 0L,
                    4, 0L,
                    5, 0L
            );
        }

    }

    public static ProductResponse from(Product product) {
        List<RatingResponse> ratingResponses = product.getRatings() == null ? List.of()
                : product.getRatings().stream()
                        .map(RatingResponse::from)
                        .toList();
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getStock(),
            product.getPrice(),
            product.getDiscountPercentage(),
            product.getFinalPrice(),
            product.getOwner().getUsername(),
            product.getCategories().stream()
                    .map(Category::getDescription)
                    .toList(),
            product.getFileImages() == null ? List.of()
                : product.getFileImages().stream().map(ProductImage::getId).toList(),
            ratingResponses
        );
    }
}