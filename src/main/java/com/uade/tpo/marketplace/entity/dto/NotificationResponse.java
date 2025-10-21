package com.uade.tpo.marketplace.entity.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String recipient;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    private boolean success;
}
