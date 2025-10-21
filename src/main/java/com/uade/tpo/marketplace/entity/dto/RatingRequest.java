package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private int value;
    private String comment;
}