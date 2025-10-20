package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.CategoryImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface CategoryImageService {

    CategoryImage uploadOrReplaceForCategory(Long categoryId, MultipartFile file, User currentUser) throws IOException;

    CategoryImage getByCategory(Long categoryId);

    void deleteByCategory(Long categoryId, User currentUser) throws AccessDeniedException;

    // Métodos originales, por compatibilidad si los usás en otro lado
    CategoryImage uploadForCategory(Long categoryId, MultipartFile file, User currentUser) throws IOException;

    CategoryImage getImage(Long imageId);

    void deleteImage(Long imageId, User currentUser);

    CategoryImage listByCategory(Long categoryId);
}
