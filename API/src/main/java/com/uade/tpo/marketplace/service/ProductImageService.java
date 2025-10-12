package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.entity.ProductImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface ProductImageService {
    ProductImage uploadForProduct(Long productId, MultipartFile file, User currentUser) throws IOException;
    ProductImage getImage(Long imageId);
    void deleteImage(Long imageId, User currentUser) throws AccessDeniedException;
    List<ProductImage> listByProduct(Long productId);
}
