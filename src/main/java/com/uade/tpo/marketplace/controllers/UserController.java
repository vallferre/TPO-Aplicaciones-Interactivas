package com.uade.tpo.marketplace.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.UserImage;
import com.uade.tpo.marketplace.entity.dto.UserEditRequest;
import com.uade.tpo.marketplace.entity.dto.UserRequest;
import com.uade.tpo.marketplace.entity.dto.UserResponse;
import com.uade.tpo.marketplace.exceptions.UserDuplicateException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.UserImageRepository;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.UserImageService;
import com.uade.tpo.marketplace.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserImageService imageService;

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

    @GetMapping("/role")
    public ResponseEntity<Object> getUserRole(
        @RequestHeader("Authorization") String authHeader) {
        // Validar header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        

        return ResponseEntity.ok(Map.of("role", requester.getRole().name()));
    }

    
    @GetMapping("/")
    public ResponseEntity<UserResponse> getUserByToken(
        @RequestHeader("Authorization") String authHeader) {
        
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
        
        // Obtener el UserResponse desde el service
        UserResponse response = userService.getUserById(requester.getId(), requester);

        // Devolver la respuesta
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
    public ResponseEntity<Object> createUser(@RequestPart("user") UserRequest userRequest, @RequestPart(value = "fileImage", required = false) MultipartFile fileImage) throws UserDuplicateException, IOException {
        User result = userService.createUser(
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getSurname(),
                userRequest.getUsername(),
                userRequest.getPassword(),
                fileImage
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

    @PutMapping("/edit")
    public ResponseEntity<Object> editUser(@RequestHeader("Authorization") String authHeader, @RequestPart("user") UserEditRequest userRequest, @RequestPart(value = "fileImage", required = false) MultipartFile fileImage) throws Exception    {
        // Validar header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
        
        UserResponse updatedUser = userService.editUser(requester, userRequest, fileImage);
        return ResponseEntity.ok(Map.of("message", "User successfully updated", "user", updatedUser));   
    }

    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        try {
            UserImage img = imageService.getByUser(userId);

            if (img == null || img.getData() == null) {
                return ResponseEntity.notFound().build();
            }

            String filename = (img.getFilename() != null && !img.getFilename().isBlank())
                    ? img.getFilename()
                    : "user-image-" + img.getId();

            MediaType contentType;
            try {
                contentType = (img.getContentType() != null)
                        ? MediaType.parseMediaType(img.getContentType())
                        : MediaType.APPLICATION_OCTET_STREAM;
            } catch (InvalidMediaTypeException e) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(contentType)
                    .contentLength(img.getSize())
                    .body(img.getData());

        } catch (EntityNotFoundException e) {
            // Si el usuario no existe o no tiene imagen asociada
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // En caso de error interno
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
}
