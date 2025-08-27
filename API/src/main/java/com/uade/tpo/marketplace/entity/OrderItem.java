package com.uade.tpo.marketplace.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //La orden a la que pertenece 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // El producto comprado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    // Cantidad de unidades de ese producto
    @Column(nullable = false)
    private int quantity;

    // Precio del producto al momento de la compra
    @Column(nullable = false)
    private double priceAtPurchase;

    // Total del ítem (cantidad * precio)
    public double getTotal() {
        return quantity * priceAtPurchase;

}
}
