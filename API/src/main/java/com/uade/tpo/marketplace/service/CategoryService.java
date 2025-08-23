package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.exceptions.CategoryDuplicateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public interface CategoryService {
    public Page<Category> getCategories(PageRequest pageRequest);

    public Optional<Category> getCategoryById(Long categoryId);

    public Category createCategory(String description) throws CategoryDuplicateException;
}
