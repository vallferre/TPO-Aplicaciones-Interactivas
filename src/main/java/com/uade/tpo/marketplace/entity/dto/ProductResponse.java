package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;

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
    private List<String> images;
    private List<String> videos;

    public ProductResponse(Long id,
                           String name,
                           String description,
                           int stock,
                           double price,
                           Double discountPercentage,
                           Double finalPrice,
                           String ownerName,
                           List<String> categories,
                           List<String> images,
                           List<String> videos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.finalPrice = finalPrice;
        this.ownerName = ownerName;
        this.categories = categories;
        this.images = images;
        this.videos = videos;
    }

    public static ProductResponse from(Product product) {
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
            product.getImages(),
            product.getVideos()
        );
    }
}
