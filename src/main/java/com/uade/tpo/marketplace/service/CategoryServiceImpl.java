package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> getCategoryByDescription(String description) {
        Category category = categoryRepository.findByDescription(description);
        if (category == null)
            return Optional.empty();
        return Optional.of(category);
    }

    @Override
    public Category createCategory(String description, User currentUser) {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        // Formatear descripción: primera letra mayúscula
        String formatted = description.substring(0, 1).toUpperCase() + 
                          description.substring(1).toLowerCase();
        
        Category category = new Category();
        category.setDescription(formatted);
        
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long categoryId, String description, User currentUser) {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        Category category = categoryOpt.get();
        
        // Formatear descripción: primera letra mayúscula
        String formatted = description.substring(0, 1).toUpperCase() + 
                          description.substring(1).toLowerCase();
        
        category.setDescription(formatted);
        
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId, User currentUser) {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            categoryRepository.delete(categoryOpt.get());
        } else {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}