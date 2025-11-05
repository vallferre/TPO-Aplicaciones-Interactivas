package com.uade.tpo.marketplace.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.dto.CategoryResponse;
import com.uade.tpo.marketplace.service.CategoryImageService;
import com.uade.tpo.marketplace.service.CategoryService;

@RestController
@RequestMapping("categories")
public class CategoriesController {

    @Autowired
    /*is an annotation used for automatic dependency injection. 
    It allows Spring to automatically resolve and inject the required dependencies (other beans) into a class at runtime, 
    eliminating the need for manual configuration of these dependencies.
    */
    private CategoryService categoryService;

    @Autowired
    private CategoryImageService imageService;

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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestParam String description,
            @RequestParam("file") MultipartFile fileImage
    ) {
        try {
            Category category = categoryService.createCategoryWithImage(description, fileImage);
            return ResponseEntity.ok(CategoryResponse.from(category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
        }
    }

    @GetMapping({"/by-description/{description}"})
    public ResponseEntity<CategoryResponse> getCategoryByDescription(@PathVariable String description) {
        Optional<Category> result = categoryService.getCategoryByDescription(description);
        if (result.isPresent())
            return ResponseEntity.ok(CategoryResponse.from(result.get()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/image")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long categoryId) {
        CategoryImage img = imageService.getByCategory(categoryId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
                        (img.getFilename() == null ? ("category-" + img.getId()) : img.getFilename()) + "\"")
                .contentType(img.getContentType() == null
                        ? MediaType.APPLICATION_OCTET_STREAM
                        : MediaType.parseMediaType(img.getContentType()))
                .contentLength(img.getSize())
                .body(img.getData());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isPresent()) {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/{categoryId}", consumes = {"multipart/form-data"})
    public ResponseEntity<CategoryResponse> editCategory(
            @PathVariable Long categoryId,
            @RequestParam String description,
            @RequestParam(value = "file", required = false) MultipartFile fileImage
    ) {
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, description, fileImage);
            return ResponseEntity.ok(CategoryResponse.from(updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
