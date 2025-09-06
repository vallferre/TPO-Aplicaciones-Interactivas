package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.OrderItem;

import lombok.Data;

@Data
public class OrderRequest {
    private Long orderId;
    private double totalAmount;
    private Long totalItems;
    private List<OrderItem> items;
}
