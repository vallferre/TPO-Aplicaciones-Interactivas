package com.uade.tpo.marketplace.entity;

import jakarta.persistence.*;
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

    public Category(){}

    public Category(String description) {
        this.description = description;
    }
}
