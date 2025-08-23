package com.uade.tpo.marketplace.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.dto.CategoryRequest;
import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.service.CategoryService;
import com.uade.tpo.marketplace.exceptions.CategoryDuplicateException;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController //marca que es una capa de tráfico
@RequestMapping("categories") 
public class CategoriesController {
    @Autowired 
    /*is an annotation used for automatic dependency injection. 
    It allows Spring to automatically resolve and inject the required dependencies (other beans) into a class at runtime, 
    eliminating the need for manual configuration of these dependencies.
    */
    private CategoryService categoryService;


    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(page, size)));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        if (result.isPresent()){
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    
    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody CategoryRequest categoryRequest)
            throws CategoryDuplicateException {
        Category result = categoryService.createCategory(
                categoryRequest.getDescription()
        );
        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }
    
    
    
    
}
