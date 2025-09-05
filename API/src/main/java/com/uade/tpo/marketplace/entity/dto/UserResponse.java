package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String email;
    private String name;
    private String surname;
    private String username;
    private List<Order> orders;
    private List<Product> products;
    private Set<Product> favoriteProducts;

    public static UserResponse full(User user){
        UserResponse res = new UserResponse();
        res.email = user.getEmail();
        res.name = user.getName();
        res.surname = user.getSurname();
        res.username = user.getUsername();
        res.orders = user.getOrders();
        res.products = user.getProducts();
        res.favoriteProducts = user.getFavoriteProducts();
        return res;
    }

    public static UserResponse limited(User user){
        UserResponse res = new UserResponse();
        res.username = user.getUsername();
        res.products = user.getProducts();
        return res;
    }
}
