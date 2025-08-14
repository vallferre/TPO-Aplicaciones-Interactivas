package com.uade.tpo.marketplace.entity;

import lombok.Builder;
import lombok.Data;

@Data //tiene todos los datos necesarios, los getters y setters
@Builder
public class Category {
    private int id;
    private String description;


}
