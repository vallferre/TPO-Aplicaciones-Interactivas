package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.CategoryImageRepository;
import com.uade.tpo.marketplace.repository.CategoryRepository;

@Service
public class CategoryImageServiceImpl implements CategoryImageService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryImageRepository imageRepository;

    @Override
    public CategoryImage uploadForCategory(Long categoryId, org.springframework.web.multipart.MultipartFile file, User currentUser) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + categoryId));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío o ausente");
        }

        CategoryImage img = new CategoryImage();
        img.setCategory(category);
        img.setFilename(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setSize(file.getSize());
        img.setData(file.getBytes());

        return imageRepository.save(img);
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
    public List<CategoryImage> listByCategory(Long categoryId) {
        return imageRepository.findByCategoryId(categoryId);
    }
}
