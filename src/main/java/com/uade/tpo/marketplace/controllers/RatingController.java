package com.uade.tpo.marketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.RatingRequest;
import com.uade.tpo.marketplace.entity.dto.RatingResponse;
import com.uade.tpo.marketplace.repository.OrderRepository;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.RatingService;

@RestController
@RequestMapping("ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtService jwtService;

    // ⭐ Promedio de un producto
    @GetMapping("/average/{productId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        double average = ratingService.getAverageRatingForProduct(productId);
        return ResponseEntity.ok(average);
    }

    // 📊 Cantidad total de ratings de un producto
    @GetMapping("/count/{productId}")
    public ResponseEntity<Integer> getNumberOfRatings(@PathVariable Long productId) {
        int count = ratingService.getNumberOfRatingsForProduct(productId);
        return ResponseEntity.ok(count);
    }

    // 🔹 Contar ratings de un producto según su valor (1-5)
    @GetMapping("/count-by-value/{productId}/{value}")
    public ResponseEntity<Integer> countRatingsByValue(
            @PathVariable Long productId,
            @PathVariable int value) {
        if (value < 1 || value > 5) {
            return ResponseEntity.badRequest().build();
        }
        int count = ratingService.countRatingsByValueForProduct(productId, value);
        return ResponseEntity.ok(count);
    }

    // ➕ Agregar rating a un producto por un usuario
    @PostMapping("/add/{productId}")
    public ResponseEntity<RatingResponse> addRating(
            @PathVariable Long productId,
            @RequestBody RatingRequest ratingRequest,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado o inválido");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        int value = ratingRequest.getValue();
        String comment = ratingRequest.getComment();

        if (value < 1 || value > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de rating inválido");
        }

        // Verificar que el usuario haya comprado el producto
        boolean hasBought = orderRepository.existsByUserIdAndProductId(user.getId(), productId);
        if (!hasBought) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes calificar un producto que no compraste");
        }

        // Guardar rating (valor + comentario)
        ratingService.addRatingToProduct(productId, user.getId(), value, comment);


        // Crear response
        RatingResponse response = new RatingResponse();
        response.setProductId(productId);
        response.setUserId(user.getId());
        response.setValue(value);
        response.setComment(comment);

        return ResponseEntity.ok(response);
    }

}