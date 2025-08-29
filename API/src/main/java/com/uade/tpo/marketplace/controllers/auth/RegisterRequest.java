package com.uade.tpo.marketplace.controllers.auth;

import com.uade.tpo.marketplace.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private User.RoleName role;
}
