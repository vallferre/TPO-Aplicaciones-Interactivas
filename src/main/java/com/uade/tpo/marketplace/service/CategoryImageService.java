package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

import com.uade.tpo.marketplace.entity.CategoryImage;

public interface CategoryImageService {
    CategoryImage uploadForCategory(Long categoryId, MultipartFile file, User currentUser) throws IOException;
    CategoryImage getImage(Long imageId);
    void deleteImage(Long imageId, User currentUser) throws AccessDeniedException;
    List<CategoryImage> listByCategory(Long categoryId);
}
