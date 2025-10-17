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

    @Override
    public void deleteCategory(Long categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            // Primero borramos la imagen si existe
            if (category.getFileImage() != null) {
                imageRepository.delete(category.getFileImage());
            }
            // Luego borramos la categoría
            categoryRepository.delete(category);
        }
    }

    @Override
    public Category updateCategory(Long categoryId, String description, MultipartFile fileImage) throws IOException {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }

        Category category = categoryOpt.get();

        // Actualizamos la descripción
        category.setDescription(description);

        // Actualizamos la imagen si se envió un archivo
        if (fileImage != null && !fileImage.isEmpty()) {
            CategoryImage categoryImage = category.getFileImage();
            if (categoryImage == null) {
                categoryImage = new CategoryImage();
            }

            categoryImage.setContentType(fileImage.getContentType());
            categoryImage.setFilename(fileImage.getOriginalFilename());
            categoryImage.setData(fileImage.getBytes());
            categoryImage.setSize(fileImage.getSize());

            // Guardamos la imagen
            categoryImage = imageRepository.save(categoryImage);

            // Asignamos la imagen actualizada a la categoría
            category.setFileImage(categoryImage);
        }

        // Guardamos la categoría actualizada
        return categoryRepository.save(category);
    }

}
