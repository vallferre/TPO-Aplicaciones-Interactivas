package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface CategoryService {
    Page<Category> getCategories(PageRequest pageRequest);

    Optional<Category> getCategoryById(Long categoryId);

    Optional<Category> getCategoryByDescription(String description);

    Category createCategory(String description, User currentUser);

    Category updateCategory(Long categoryId, String description, User currentUser);

    void deleteCategory(Long categoryId, User currentUser);
}