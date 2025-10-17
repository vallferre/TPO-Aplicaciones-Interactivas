package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String description;
    private Long fileImageId;

    public static CategoryResponse from(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setDescription(c.getDescription());
        r.setFileImageId(c.getFileImage() != null ? c.getFileImage().getId() : null);
        return r;
    }
}
