package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Integer stock;
    private Double price;

    // Nuevo nombre “oficial”
    private Double discountPercentage;

    // Compatibilidad hacia atrás (si viene ‘discount’, lo usamos como discountPercentage)
    private Double discount;

    private List<String> categories;
    private List<String> images;
    private List<String> videos;

    public Double getEffectiveDiscountPercentage() {
        if (discountPercentage != null) return discountPercentage;
        return discount; // puede ser null; lo maneja la capa de servicio/entidad
    }
}
