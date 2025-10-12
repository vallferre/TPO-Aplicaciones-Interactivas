package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;

@Data
public class CartRequest {
    private String productName;
    private Long productId;
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}
