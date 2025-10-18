package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;

@Data
public class UserEditRequest {
    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
}
