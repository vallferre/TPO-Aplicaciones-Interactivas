package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.ProductImage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageMetaResponse {
    private Long id;
    private Long productId;
    private String filename;
    private String contentType;
    private long size;

    public static ImageMetaResponse from(ProductImage img) {
        return ImageMetaResponse.builder()
                .id(img.getId())
                .productId(img.getProduct().getId())
                .filename(img.getFilename())
                .contentType(img.getContentType())
                .size(img.getSize())
                .build();
    }
}
