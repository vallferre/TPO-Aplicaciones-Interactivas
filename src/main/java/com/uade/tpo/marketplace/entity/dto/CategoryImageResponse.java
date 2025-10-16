package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.CategoryImage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryImageResponse {
    private Long id;
    private Long categoryId;
    private String filename;
    private String contentType;
    private long size;

    public static CategoryImageResponse from(CategoryImage img) {
        return CategoryImageResponse.builder()
                .id(img.getId())
                .categoryId(img.getCategory().getId())
                .filename(img.getFilename())
                .contentType(img.getContentType())
                .size(img.getSize())
                .build();
    }
}
