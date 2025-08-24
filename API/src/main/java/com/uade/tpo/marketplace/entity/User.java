package com.uade.tpo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String surname;

    @Column
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ManyToOne(fetch = FetchType.EAGER) // solo un rol por usuario
    @JoinColumn(name = "role_id")
    private Role role;

}