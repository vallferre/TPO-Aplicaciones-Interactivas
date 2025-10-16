package com.uade.tpo.marketplace.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.CategoryImageResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.CategoryImageService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryImagesController {
    
    private final CategoryImageService imageService;

    // Subir imagen a un producto (solo dueño)
    @PostMapping("/categories/{categoryId}/images")
    public ResponseEntity<CategoryImageResponse> uploadImage(
            @PathVariable Long categoryId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        CategoryImage saved = imageService.uploadForCategory(categoryId, file, currentUser);
        return ResponseEntity.ok(CategoryImageResponse.from(saved));
    }

    // Listar metadatos de imágenes de un producto
    @GetMapping("/categories/{categoryId}/images")
    public ResponseEntity<List<CategoryImageResponse>> listImages(@PathVariable Long categoryId) {
        List<CategoryImageResponse> list = imageService.listByCategory(categoryId)
                .stream()
                .map(CategoryImageResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    // Descargar/ver imagen por id
    @GetMapping("/categories/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        CategoryImage img = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (img.getFilename() == null ? ("image-" + img.getId()) : img.getFilename()) + "\"")
                .contentType(img.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(img.getContentType()))
                .contentLength(img.getSize())
                .body(img.getData());
    }

    // Eliminar imagen por id (solo dueño del producto)
    @DeleteMapping("/categories/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId,
                                            @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        imageService.deleteImage(imageId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
