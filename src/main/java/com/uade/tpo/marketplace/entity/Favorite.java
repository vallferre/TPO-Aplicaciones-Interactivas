package com.uade.tpo.marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ID del producto (sin relación directa si los productos están en otro microservicio)
    @Column(name = "product_id", nullable = false)
    private Long productId;
}

