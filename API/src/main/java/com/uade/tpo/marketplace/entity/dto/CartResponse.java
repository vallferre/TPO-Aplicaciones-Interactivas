package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import com.uade.tpo.marketplace.entity.CartItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
    private List<CartItem> items;
    private double total;
}
