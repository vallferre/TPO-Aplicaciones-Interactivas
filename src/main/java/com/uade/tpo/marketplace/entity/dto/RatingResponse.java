package com.uade.tpo.marketplace.entity.dto;

import com.uade.tpo.marketplace.entity.Rating;

import lombok.Data;

@Data
public class RatingResponse {
    private Long productId;
    private Long userId;
    private int value;
    private String username;
    private String comment;

    public static RatingResponse from(Rating rating) {
        RatingResponse r = new RatingResponse();
        r.setProductId(rating.getProduct().getId());
        r.setUserId(rating.getUser().getId());
        r.setValue(rating.getValue());
        r.setUsername(rating.getUser().getUsername());
        r.setComment(rating.getComment());
        return r;
    }
}