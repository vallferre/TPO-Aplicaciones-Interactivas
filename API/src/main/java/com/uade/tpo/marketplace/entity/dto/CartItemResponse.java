package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long productId;
    private int quantity;
    private String productName;
    private String productDescription;
    private double priceAtAddTime;

    public static CartItemResponse from(CartItem cartItem) {
        CartItemResponse res = new CartItemResponse();
        res.productId = cartItem.getProduct().getId();
        res.productName = cartItem.getProduct().getName();
        res.productDescription = cartItem.getProduct().getDescription();
        res.quantity = cartItem.getQuantity();
        res.priceAtAddTime = cartItem.getPriceAtAddTime();
        return res;
    }
}
