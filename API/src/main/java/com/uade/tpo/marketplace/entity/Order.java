package com.uade.tpo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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
}
