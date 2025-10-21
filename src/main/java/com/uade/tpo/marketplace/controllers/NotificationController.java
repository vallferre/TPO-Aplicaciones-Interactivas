package com.uade.tpo.marketplace.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.marketplace.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Endpoint para notificar a los usuarios que tienen el producto en favoritos
    @PostMapping("/product/{productId}")
    public ResponseEntity<String> notifyFavorites(@PathVariable Long productId) {
        // Llama al service para enviar la notificación
        notificationService.notifyUsersByProductId(productId);

        // Retorna un mensaje simple de éxito
        return ResponseEntity.ok("Notificaciones enviadas correctamente para el producto ID: " + productId);
    }
}
