package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.CategoryImageRepository;
import com.uade.tpo.marketplace.repository.CategoryRepository;

@Service
public class CategoryImageServiceImpl implements CategoryImageService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryImageRepository imageRepository;

    /**
     * Sube o reemplaza la imagen de una categoría (solo 1 imagen por categoría)
     */
    @Override
    public CategoryImage uploadOrReplaceForCategory(Long categoryId, MultipartFile file, User currentUser) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoryId));

        // Borra imagen existente
        CategoryImage existing = imageRepository.findByCategoryId(categoryId);
        if (existing != null) {
            imageRepository.delete(existing);
        }

        CategoryImage img = new CategoryImage();
        img.setCategory(category);
        img.setFilename(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setSize(file.getSize());
        img.setData(file.getBytes());

        return imageRepository.save(img);
    }



    /**
     * Obtiene la imagen única de una categoría
     */
    @Override
    public CategoryImage getByCategory(Long categoryId) {
        CategoryImage image = imageRepository.findByCategoryId(categoryId);
        if (image == null) {
            throw new RuntimeException("La categoría no tiene imagen asociada.");
        }
        return image;
    }

    /**
     * Elimina la imagen asociada a una categoría
     */
    @Override
    public void deleteByCategory(Long categoryId, User currentUser) throws AccessDeniedException {
        CategoryImage existing = imageRepository.findByCategoryId(categoryId);
        if (existing == null) {
            throw new RuntimeException("No se encontró imagen para la categoría id: " + categoryId);
        }

        imageRepository.delete(existing);
    }

    // Métodos antiguos mantenidos para compatibilidad si se usan en otro lugar
    @Override
    public CategoryImage uploadForCategory(Long categoryId, org.springframework.web.multipart.MultipartFile file, User currentUser) throws IOException {
        return uploadOrReplaceForCategory(categoryId, file, currentUser);
    }

    @Override
    public CategoryImage getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));
    }

    @Override
    public void deleteImage(Long imageId, User currentUser) {
        CategoryImage img = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));

        imageRepository.deleteById(imageId);
    }

    @Override
    public CategoryImage listByCategory(Long categoryId) {
        return imageRepository.findByCategoryId(categoryId);
    }
}