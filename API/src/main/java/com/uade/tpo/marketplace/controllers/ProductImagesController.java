package com.uade.tpo.marketplace.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.ProductImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.ImageMetaResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductImagesController {

    private final ProductImageService imageService;

    // Subir imagen a un producto (solo dueño)
    @PostMapping("/products/{productId}/images")
    public ResponseEntity<ImageMetaResponse> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {

        ProductImage saved = imageService.uploadForProduct(productId, file, currentUser);
        return ResponseEntity.ok(ImageMetaResponse.from(saved));
    }

    // Listar metadatos de imágenes de un producto
    @GetMapping("/products/{productId}/images")
    public ResponseEntity<List<ImageMetaResponse>> listImages(@PathVariable Long productId) {
        List<ImageMetaResponse> list = imageService.listByProduct(productId)
                .stream()
                .map(ImageMetaResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    // Descargar/ver imagen por id
    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        ProductImage img = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (img.getFilename() == null ? ("image-" + img.getId()) : img.getFilename()) + "\"")
                .contentType(img.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(img.getContentType()))
                .contentLength(img.getSize())
                .body(img.getData());
    }

    // Eliminar imagen por id (solo dueño del producto)
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId,
                                            @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        imageService.deleteImage(imageId, currentUser);
        return ResponseEntity.noContent().build();
    }
    
}
