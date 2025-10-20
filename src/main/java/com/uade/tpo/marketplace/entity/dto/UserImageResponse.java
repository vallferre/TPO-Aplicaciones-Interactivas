package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.UserImage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImageResponse {
    private Long id;
    private Long userId;
    private String filename;
    private String contentType;
    private long size;

    public static UserImageResponse from(UserImage img) {
        return UserImageResponse.builder()
                .id(img.getId())
                .userId(img.getUser().getId())
                .filename(img.getFilename())
                .contentType(img.getContentType())
                .size(img.getSize())
                .build();
    }
}
