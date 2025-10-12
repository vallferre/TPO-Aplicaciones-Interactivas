package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    // 👈 Necesitamos el id para guardarlo en el front
    private Long id;

    private String email;
    private String name;
    private String surname;
    private String username;

    // Estos campos pueden ir nulos (por eso el @JsonInclude NON_NULL)
    private List<?> orders;
    private List<ProductResponse> products;
    private Set<Product> favoriteProducts;

    /** Respuesta mínima (ideal para /users/current) */
    public static UserResponse from(User user) {
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.email = user.getEmail();
        res.name = user.getName();
        res.surname = user.getSurname();
        res.username = user.getUsername();
        return res;
    }

    /** Respuesta completa si alguna vista necesita todo */
    public static UserResponse full(User user, List<Product> products){
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.email = user.getEmail();
        res.name = user.getName();
        res.surname = user.getSurname();
        res.username = user.getUsername();
        res.orders = user.getOrders();
        res.products = products.stream()
            .map(ProductResponse::from)
            .toList();
        res.favoriteProducts = user.getFavoriteProducts();
        return res;
    }

    /** Respuesta reducida con username + products */
    public static UserResponse limited(User user, List<Product> products){
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.username = user.getUsername();
        res.products = products.stream()
            .map(ProductResponse::from)
            .toList();
        return res;
    }
}
