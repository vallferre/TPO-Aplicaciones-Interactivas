package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.ProductImage;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long productId;
    private int quantity;
    private String productName;
    private String productDescription;
    private double priceAtAddTime;
    private ProductImage productImageUrl;

    public static CartItemResponse from(CartItem cartItem) {
        CartItemResponse res = new CartItemResponse();
        res.productId = cartItem.getProduct().getId();
        res.productName = cartItem.getProduct().getName();
        res.productDescription = cartItem.getProduct().getDescription();
        res.quantity = cartItem.getQuantity();
        res.priceAtAddTime = cartItem.getPriceAtAddTime();
        res.productImageUrl = cartItem.getProduct().getFileImages() != null && !cartItem.getProduct().getFileImages().isEmpty()
            ? cartItem.getProduct().getFileImages().get(0)  // Asume que la primera imagen es la principal
            : null;
        return res;
    }
}
