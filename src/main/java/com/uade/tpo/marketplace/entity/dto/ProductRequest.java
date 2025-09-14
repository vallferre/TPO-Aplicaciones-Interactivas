package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private Double discount;
    private List<String> categories;
    private List<String> images;
    private List<String> videos;
}
