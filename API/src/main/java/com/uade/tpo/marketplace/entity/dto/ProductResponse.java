package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductResponse {
    private String name;
    private String description;
    private int stock;
    private double price;
    private List<String> categories; //.
    private List<String> images;
    private List<String> videos;

    public ProductResponse(String name,
                       String description,
                       int stock,
                       double price,
                       List<String> categories,
                       List<String> images,
                       List<String> videos) {
    this.name = name;
    this.description = description;
    this.stock = stock;
    this.price = price;
    this.categories = categories;
    this.images = images;
    this.videos = videos;
}
}
