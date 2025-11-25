package com.uade.tpo.marketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.service.CategoryImageService;
import com.uade.tpo.marketplace.service.CategoryService;

import java.util.Optional;

@RestController
@RequestMapping("categories")
public class CategoryImageController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryImageService imageService;

    // GET - Obtener imagen de categoría (público)
    @GetMapping("/{categoryId}/image")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long categoryId) {
        try {
            CategoryImage img = imageService.getByCategory(categoryId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
                            (img.getFilename() != null ? img.getFilename() : ("category-" + img.getId())) + "\"")
                    .contentType(img.getContentType() != null
                            ? MediaType.parseMediaType(img.getContentType())
                            : MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(img.getSize())
                    .body(img.getData());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Subir/Reemplazar imagen de categoría (requiere ADMIN)
    @PostMapping(value = "/{categoryId}/image", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadCategoryImage(
            @PathVariable Long categoryId,
            @RequestParam("image") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            imageService.saveImage(categoryOpt.get(), file, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }

    // DELETE - Eliminar imagen de categoría (requiere ADMIN)
    @DeleteMapping("/{categoryId}/image")
    public ResponseEntity<?> deleteCategoryImage(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            imageService.deleteByCategory(categoryId, currentUser);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Si no hay imagen, también devolvemos 204
            return ResponseEntity.noContent().build();
        }
    }
}