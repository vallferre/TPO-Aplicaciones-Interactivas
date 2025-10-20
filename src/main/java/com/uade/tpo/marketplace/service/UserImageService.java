package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.UserImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface UserImageService {
    UserImage uploadOrReplaceForUser(MultipartFile file, User currentUser) throws IOException;

    UserImage getByUser(Long userId);

    void deleteByUser(User currentUser) throws AccessDeniedException;

    // Métodos originales, por compatibilidad si los usás en otro lado
    UserImage uploadForUser(MultipartFile file, User currentUser) throws IOException;

    UserImage getImage(Long imageId);

    void deleteImage(Long imageId, User currentUser);

    UserImage listByUser(Long userId);
}
