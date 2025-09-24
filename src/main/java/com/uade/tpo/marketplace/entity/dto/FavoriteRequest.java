package com.uade.tpo.marketplace.entity.dto;

public class FavoriteRequest {
    private String productName;
    private Long productId;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}

