package com.uade.tpo.marketplace.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String recipient;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    private boolean success;
}
