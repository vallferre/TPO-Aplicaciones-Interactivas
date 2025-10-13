package com.uade.tpo.marketplace.controllers;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.UserRequest;
import com.uade.tpo.marketplace.entity.dto.UserResponse;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/email/{userEmail}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable("userEmail") String userEmail, @AuthenticationPrincipal User requester) {
        Optional<UserResponse> result = userService.getUserByEmail(userEmail, requester);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());  // devuelve el usuario directamente
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found")); // mensaje de error si no existe
        }
    }

    //CAMBIE ESTO
    @GetMapping("/")
    public ResponseEntity<UserResponse> getUserByToken(
        @RequestHeader("Authorization") String authHeader) {
        
        // 1️⃣ Validar header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2️⃣ Extraer token
        String token = authHeader.substring(7);

        // 3️⃣ Extraer username del token
        String username = jwtService.extractUsername(token);
        
        // 4️⃣ Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        
        // 5️⃣ Obtener el UserResponse desde el service
        UserResponse response = userService.getUserById(requester.getId(), requester);

        // 6️⃣ Devolver la respuesta
        return ResponseEntity.ok(response);
    }




    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok(Map.of("message", "User successfully deleted"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }


    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) throws UserDuplicateException {
        User result = userService.createUser(
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getSurname(),
                userRequest.getUsername(),
                userRequest.getPassword()
        );
        return ResponseEntity.created(URI.create("/users/" + result.getId()))
                .body(Map.of("message", "User successfully created", "user", result));
    }

    @GetMapping("/specific-username/{username}")
    public ResponseEntity<Object> getSpecificUsername(@PathVariable String username, @AuthenticationPrincipal User requester) {
        Optional<UserResponse> result = userService.getUserByUsername(username, requester);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());  // devuelve el usuario directamente
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found")); // mensaje de error si no existe
        }
    }
    
}
