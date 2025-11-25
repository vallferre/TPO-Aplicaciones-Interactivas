package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;

public interface CategoryImageService {

    // Guardar imagen (crea o reemplaza) - requiere ADMIN
    CategoryImage saveImage(Category category, MultipartFile file, User currentUser) throws IOException;

    // Obtener imagen por categoría (público)
    CategoryImage getByCategory(Long categoryId);

    // Eliminar imagen por ID - requiere ADMIN
    void deleteImage(Long imageId, User currentUser);

    // Eliminar imagen por categoría - requiere ADMIN
    void deleteByCategory(Long categoryId, User currentUser);
}