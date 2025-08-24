package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String description;
    private int stock;
    private double price;
    private Long categoryId; // referenciamos la categoría por su ID
}
