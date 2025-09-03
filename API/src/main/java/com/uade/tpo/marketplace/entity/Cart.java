package com.uade.tpo.marketplace.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cart asociado a un único usuario
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Lista de ítems del carrito
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();

    @Column
    private double total;

    // Total del carrito (opcional si lo querés precalculado)
    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtAddTime())
                .sum();
    }

    // Agregar un producto al carrito
    public void addProduct(Product product, int quantity) {
        // Si ya está en el carrito, actualizar la cantidad
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                total = getTotal();
                return;
            }
        }
        // Si no está, crear uno nuevo
        CartItem newItem = new CartItem();
        newItem.setCart(this);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setPriceAtAddTime(product.getPrice()); // congelamos el precio
        total = getTotal();
        items.add(newItem);
    }

    // Quitar un producto completamente
    public void removeProduct(Product product) {
        items.removeIf(item -> item.getProduct().getId().equals(product.getId()));
        total = total - product.getPrice();
    }

    // Vaciar carrito
    public void clear() {
        items.clear();
        total = 0;
    }
}