package com.uade.tpo.marketplace.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El carrito al que pertenece este ítem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    // El producto agregado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    // Cantidad de unidades de ese producto
    @Column(nullable = false)
    private int quantity;

    // Precio del producto al momento de agregarlo al carrito (opcional, pero útil si el precio puede cambiar)
    @Column(nullable = false)
    private double priceAtAddTime;

    // Fecha de agregado (opcional)
    @Column(name = "added_at")
    private java.time.LocalDateTime addedAt;

    @PrePersist
    public void prePersist() {
        if (addedAt == null) {
            addedAt = java.time.LocalDateTime.now();
        }
        if (priceAtAddTime == 0 && product != null) {
            priceAtAddTime = product.getPrice();
        }
    }
}

