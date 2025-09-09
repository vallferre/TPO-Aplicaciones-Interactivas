package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.OrderItem;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long productId;
    private String description;
    private int quantity;
    private double priceAtPurchase;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse();
        res.productId = item.getProduct().getId();
        res.description = item.getProduct().getDescription();
        res.quantity = item.getQuantity();
        res.priceAtPurchase = item.getPriceAtPurchase();
        return res;
    }
}
