package com.uade.tpo.marketplace.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.Data;

@Data
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private int stock;

    @Column
    private double price;

    // Descuento persistido
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;

    // Precio final persistido
    @Column(name = "final_price")
    private Double finalPrice;

    @ManyToMany
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orders"})
    private User owner;

    // NUEVO: relación a imágenes por archivo
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductImage> fileImages = new ArrayList<>();

    // VIDEOS se mantienen
    @ElementCollection
    @CollectionTable(name = "product_videos", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "video_url")
    private List<String> videos = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        double basePrice = (this.price != 0) ? this.price : 0.0;
        double d = (this.discountPercentage == null) ? 0.0 : this.discountPercentage;

        if (d < 0) d = 0;
        if (d > 100) d = 100;

        this.discountPercentage = d;
        this.finalPrice = basePrice - (basePrice * d / 100.0);
    }
}
