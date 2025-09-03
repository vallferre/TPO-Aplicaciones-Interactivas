package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private int stock;
    private double price;
    private List<String> categories; //.
    private List<String> images;
    private List<String> videos;
}
