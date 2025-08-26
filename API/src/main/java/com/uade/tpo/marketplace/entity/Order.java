package com.uade.tpo.marketplace.entity;

import java.util.*;

import org.springframework.cglib.core.Local;

import jakarta.persistence.*;
import lombok.Data;

@Data //tiene todos los datos necesarios, los getters y setters
@Entity
@Table(name = "Orders") //solo se renombra en sql
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long count;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) //es la foreign key de la tabla
    private User user;

    //lista de items de la orden
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

}
