package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private double total;

    public CartResponse(Cart cart, List<CartItem> products) {
        this.cartId = cart.getId();
        this.userId = cart.getUser().getId();
        this.total = cart.getTotal();
        this.items = products.stream()
            .map(CartItemResponse::from)  // usa el mapper estático
            .toList();
    }
}
