package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.CategoryImageRepository;
import com.uade.tpo.marketplace.repository.CategoryRepository;

@Service
public class CategoryImageServiceImpl implements CategoryImageService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryImageRepository imageRepository;

    @Override
    public CategoryImage saveImage(Category category, MultipartFile file, User currentUser) throws IOException {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        // Eliminar imagen existente si la hay
        CategoryImage existing = imageRepository.findByCategoryId(category.getId());
        if (existing != null) {
            imageRepository.delete(existing);
        }

        // Crear nueva imagen
        CategoryImage img = new CategoryImage();
        img.setCategory(category);
        img.setFilename(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setSize(file.getSize());
        img.setData(file.getBytes());

        CategoryImage saved = imageRepository.save(img);

        // Actualizar referencia en la categoría
        category.setFileImage(saved);
        categoryRepository.save(category);

        return saved;
    }

    @Override
    public CategoryImage getByCategory(Long categoryId) {
        CategoryImage image = imageRepository.findByCategoryId(categoryId);
        if (image == null) {
            throw new RuntimeException("La categoría no tiene imagen asociada.");
        }
        return image;
    }

    @Override
    public void deleteImage(Long imageId, User currentUser) {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        CategoryImage img = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));

        // Limpiar referencia en la categoría
        if (img.getCategory() != null) {
            Category category = img.getCategory();
            category.setFileImage(null);
            categoryRepository.save(category);
        }

        imageRepository.deleteById(imageId);
    }

    @Override
    public void deleteByCategory(Long categoryId, User currentUser) {
        if (currentUser == null || !isAdmin(currentUser)) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción");
        }
        
        CategoryImage existing = imageRepository.findByCategoryId(categoryId);
        if (existing == null) {
            throw new RuntimeException("No se encontró imagen para la categoría id: " + categoryId);
        }

        // Limpiar referencia en la categoría
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        category.setFileImage(null);
        categoryRepository.save(category);

        imageRepository.delete(existing);
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}