package com.uade.tpo.marketplace.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;


@Data //tiene todos los datos necesarios, los getters y setters
//@AllArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String description;

    @ManyToMany(mappedBy = "categories") //para que se pueda filtrar por categoría y conseguir todos los productos de esa categoría
    private List<Product> products = new ArrayList<>();

    public Category(){}

    public Category(String description) {
        this.description = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
    }
}
