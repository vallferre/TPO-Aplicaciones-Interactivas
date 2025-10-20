package com.uade.tpo.marketplace.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.UserImage;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.UserImageRepository;

@Service
public class UserImageServiceImpl implements UserImageService{
    @Autowired
    private UserImageRepository imageRepository;

    /**
     * Sube o reemplaza la imagen de un usuario (solo 1 imagen por usuario)
     */
    @Override
    public UserImage uploadOrReplaceForUser(MultipartFile file, User currentUser) throws IOException {
        // Borra imagen existente
        UserImage existing = imageRepository.findByUserId(currentUser.getId());
        if (existing != null) {
            imageRepository.delete(existing);
        }

        UserImage img = new UserImage();
        img.setUser(currentUser);
        img.setFilename(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setSize(file.getSize());
        img.setData(file.getBytes());

        return imageRepository.save(img);
    }

    /**
     * Obtiene la imagen única de un usuario
     */
    @Override
    public UserImage getByUser(Long userId) {
        UserImage image = imageRepository.findByUserId(userId);
        if (image == null) {
            throw new RuntimeException("El usuario no tiene imagen asociada.");
        }
        return image;
    }

    /**
     * Elimina la imagen asociada a un usuario
     */
    @Override
    public void deleteByUser(User currentUser) throws AccessDeniedException {
        UserImage existing = imageRepository.findByUserId(currentUser.getId());
        if (existing == null) {
            throw new RuntimeException("No se encontró imagen para la categoría id: " + currentUser.getId());
        }

        imageRepository.delete(existing);
    }

    // Métodos antiguos mantenidos para compatibilidad si se usan en otro lugar
    @Override
    public UserImage uploadForUser(MultipartFile file, User currentUser) throws IOException {
        return uploadOrReplaceForUser(file, currentUser);
    }

    @Override
    public UserImage getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));
    }

    @Override
    public void deleteImage(Long imageId, User currentUser) {
        UserImage img = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));

        imageRepository.deleteById(imageId);
    }

    @Override
    public UserImage listByUser(Long userId) {
        return imageRepository.findByUserId(userId);
    }
}
