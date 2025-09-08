package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;

import lombok.Data;

@Data
public class ProductResponse {
    private String name;
    private String description;
    private int stock;
    private double price;
    private String ownerName;
    private List<String> categories; //.
    private List<String> images;
    private List<String> videos;

    public ProductResponse(String name,
                        String description,
                        int stock,
                        double price,
                        String ownerName,
                        List<String> categories,
                        List<String> images,
                        List<String> videos) {
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.ownerName = ownerName;
        this.categories = categories;
        this.images = images;
        this.videos = videos;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getName(),
            product.getDescription(),
            product.getStock(),
            product.getPrice(),
            product.getOwner().getUserN(),
            product.getCategories().stream()
                                     .map(Category::getDescription) // convertir categorías
                                     .toList(),
            product.getImages(),
            product.getVideos()
        );
    }

}
