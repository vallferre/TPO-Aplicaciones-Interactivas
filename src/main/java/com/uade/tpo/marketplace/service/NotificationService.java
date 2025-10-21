package com.uade.tpo.marketplace.service;

public interface NotificationService {
    void notifyUsersByProductId(Long productId);
    void sendWelcomeNotification(String email, String username);
}
