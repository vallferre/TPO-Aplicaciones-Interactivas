package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.OrderItem;
import com.uade.tpo.marketplace.entity.ProductImage;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long productIdSnapshot;
    private String description;
    private int quantity;
    private double priceAtPurchase;
    private double discountedPriceAtPurchase = 0;
    private ProductImage productImageUrl;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse();
        res.productIdSnapshot = item.getProductIdSnapshot();
        res.description = item.getProduct().getDescription();
        res.quantity = item.getQuantity();
        res.priceAtPurchase = item.getPriceAtPurchase();
        res.discountedPriceAtPurchase = item.getProduct().getDiscountPercentage();
        res.productImageUrl= item.getProduct().getFileImages() != null && !item.getProduct().getFileImages().isEmpty()
            ? item.getProduct().getFileImages().get(0)  // Asume que la primera imagen es la principal
            : null;
        return res;
    }
}