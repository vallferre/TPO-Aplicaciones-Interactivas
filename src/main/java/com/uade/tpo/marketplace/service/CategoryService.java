package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CategoryService {
    public Page<Category> getCategories(PageRequest pageRequest);

    public Optional<Category> getCategoryById(Long categoryId);

    public Optional<Category> getCategoryByDescription(String description);

    public Category createCategoryWithImage(String description, MultipartFile fileImage) throws IOException;

    public CategoryImage getCategoryImageById(Long imageId);

    public void deleteCategory(Long categoryId);

    public Category updateCategory(Long categoryId, String description, MultipartFile fileImage) throws IOException;
}
