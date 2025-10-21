package com.uade.tpo.marketplace.mapper;

import org.springframework.stereotype.Component;

import com.uade.tpo.marketplace.entity.Notification;
import com.uade.tpo.marketplace.entity.dto.NotificationRequest;
import com.uade.tpo.marketplace.entity.dto.NotificationResponse;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    // Request → Model
    public Notification toModel(NotificationRequest dto) {
        return Notification.builder()
                .recipient(dto.getRecipient())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .sentAt(LocalDateTime.now())
                .build();
    }

    // Model → Response
    public NotificationResponse toResponse(Notification model) {
        return NotificationResponse.builder()
                .recipient(model.getRecipient())
                .subject(model.getSubject())
                .message(model.getMessage())
                .sentAt(model.getSentAt())
                .success(model.isSuccess())
                .build();
    }
}
