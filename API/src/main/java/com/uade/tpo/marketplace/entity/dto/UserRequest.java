package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.Role;

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
    private Role role;
}
