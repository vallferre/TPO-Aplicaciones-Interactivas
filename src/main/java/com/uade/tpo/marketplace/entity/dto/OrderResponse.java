package com.uade.tpo.marketplace.entity.dto;

import java.time.LocalDate;
import java.util.List;

import com.uade.tpo.marketplace.entity.Order;

import lombok.Data;

@Data
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private double totalAmount;
    private long count;
    private LocalDate orderDate;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUser().getId();
        this.totalAmount = order.getTotalAmount();
        this.count = order.getCount();
        this.orderDate = order.getOrderDate();
        this.items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    public static OrderResponse from(Order order) {
    return new OrderResponse(order);
}

}

