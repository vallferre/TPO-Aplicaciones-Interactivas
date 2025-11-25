package com.uade.tpo.marketplace.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.CategoryRequest;
import com.uade.tpo.marketplace.entity.dto.CategoryResponse;
import com.uade.tpo.marketplace.service.CategoryService;

@RestController
@RequestMapping("categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<Category> categoriesPage;
        if (page == null || size == null)
            categoriesPage = categoryService.getCategories(PageRequest.of(0, Integer.MAX_VALUE));
        else
            categoriesPage = categoryService.getCategories(PageRequest.of(page, size));

        Page<CategoryResponse> responsePage = categoriesPage.map(CategoryResponse::from);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        if (result.isPresent())
            return ResponseEntity.ok(CategoryResponse.from(result.get()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-description/{description}")
    public ResponseEntity<CategoryResponse> getCategoryByDescription(@PathVariable String description) {
        Optional<Category> result = categoryService.getCategoryByDescription(description);
        if (result.isPresent())
            return ResponseEntity.ok(CategoryResponse.from(result.get()));

        return ResponseEntity.noContent().build();
    }

    // POST - Crear categoría (SOLO DESCRIPCIÓN, JSON)
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Category category = categoryService.createCategory(request.getDescription(), currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la categoría: " + e.getMessage());
        }
    }

    // PUT - Actualizar categoría (SOLO DESCRIPCIÓN, JSON)
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> editCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, request.getDescription(), currentUser);
            return ResponseEntity.ok(CategoryResponse.from(updatedCategory));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            categoryService.deleteCategory(categoryId, currentUser);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la categoría: " + e.getMessage());
        }
    }
}