package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;

@Data
public class CartRequest {
    private String productName;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
