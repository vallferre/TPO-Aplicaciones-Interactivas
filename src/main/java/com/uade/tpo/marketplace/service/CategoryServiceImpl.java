package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.exceptions.CategoryDuplicateException;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(String description) throws CategoryDuplicateException {
        Category category = categoryRepository.findByDescription(description);
        if (category == null)
            return categoryRepository.save(new Category(description));
        throw new CategoryDuplicateException("La categoría '" + description + "' ya existe.");

    }

    @Override
    public Optional<Category> getCategoryByDescription(String description) {
        Category category = categoryRepository.findByDescription(description);
        if (category == null)
            return Optional.empty();
        return Optional.of(category);
    }
}
