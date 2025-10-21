package com.uade.tpo.marketplace.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import com.uade.tpo.marketplace.controllers.auth.RegisterRequest;
import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.NotificationService;
import com.uade.tpo.marketplace.service.UserImageService;
import com.uade.tpo.marketplace.service.UserService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    // Endpoint para notificar a los usuarios que tienen el producto en favoritos
    @PostMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> notifyFavorites(@PathVariable Long productId) {
        // Llama al service para enviar la notificación
        notificationService.notifyUsersByProductId(productId);

        // Retorna un mensaje simple de éxito
        return ResponseEntity.ok("Notificaciones enviadas correctamente para el producto ID: " + productId);
    }

    @PostMapping("/welcome")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestHeader("Authorization") String authHeader) {
        try {
            // Validar header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Extraer token
            String token = authHeader.substring(7);

            // Extraer username del token
            String username = jwtService.extractUsername(token);
            
            // Buscar el usuario en la base
            User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));

            // Enviar correo de bienvenida
            notificationService.sendWelcomeNotification(requester.getEmail(), requester.getUsername());

            // Devolver respuesta
            return ResponseEntity.ok("Notificación enviada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar notificacion al usuario: " + e.getMessage());
        }
    }
}
