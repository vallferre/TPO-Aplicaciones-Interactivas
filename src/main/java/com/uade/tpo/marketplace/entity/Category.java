package com.uade.tpo.marketplace.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String description;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    // Relación OneToOne con CategoryImage
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private CategoryImage fileImage;

    public Category() {
    }

    public Category(String description) {
        this.description = description;
    }
}