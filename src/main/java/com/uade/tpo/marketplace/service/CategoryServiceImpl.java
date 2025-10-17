package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.exceptions.CategoryDuplicateException;
import com.uade.tpo.marketplace.repository.CategoryImageRepository;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryImageRepository imageRepository;

    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

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
    public Category createCategoryWithImage(String description, MultipartFile fileImage) throws IOException {
        CategoryImage categoryImage = null;

        if (fileImage != null && !fileImage.isEmpty()) {
            categoryImage = new CategoryImage();
            categoryImage.setContentType(fileImage.getContentType());
            categoryImage.setFilename(fileImage.getOriginalFilename());
            categoryImage.setData(fileImage.getBytes());
            categoryImage.setSize(fileImage.getSize());
            categoryImage = imageRepository.save(categoryImage); // guardo la imagen primero
        }

        Category category = new Category(description, categoryImage);
        return categoryRepository.save(category); // guardo la categoría
    }

    @Override
    public CategoryImage getCategoryImageById(Long imageId) {
        return categoryRepository.findImageByFileImageId(imageId);
    }
}
