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
        res.description = item.getProductDescriptionSnapshot();
        res.quantity = item.getProductQuantitySnapshot();
        res.priceAtPurchase = item.getProductPriceSnapshot();
        res.discountedPriceAtPurchase = item.getProductDiscountedPriceSnapshot();
        if (item.getProduct() != null) {
            res.productImageUrl = item.getProduct().getFileImages() != null && !item.getProduct().getFileImages().isEmpty()
                ? item.getProduct().getFileImages().get(0)
                : null;
        } else {
            // Product was deleted, no image available
            res.productImageUrl = null;
        }
        return res;
    }
}