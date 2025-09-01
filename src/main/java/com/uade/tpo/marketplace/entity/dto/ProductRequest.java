package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private int stock;
    private double price;
    private Long categoryId;
    private List<String> images;
    private List<String> videos;
}
