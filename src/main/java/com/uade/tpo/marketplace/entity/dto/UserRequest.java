package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.Set;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;

import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private String username;
    private String password;
    private List<Order> orders;
    private User.RoleName role;
    private Set<Product> favoriteProducts;
}
