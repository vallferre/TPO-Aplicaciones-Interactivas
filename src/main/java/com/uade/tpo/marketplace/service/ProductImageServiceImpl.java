package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.ProductImage;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.InvalidStockException;
import com.uade.tpo.marketplace.repository.ProductImageRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository imageRepository;

    @Override
    public ProductImage uploadForProduct(Long productId, org.springframework.web.multipart.MultipartFile file, User currentUser) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        // Seguridad: solo el dueño del producto puede subir imágenes
        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para subir imagenes en este producto");
        }

        if (product.getStock() < 1) {
            throw new InvalidStockException("El stock no puede ser negativo");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío o ausente");
        }

        ProductImage img = new ProductImage();
        img.setProduct(product);
        img.setFilename(file.getOriginalFilename());
        img.setContentType(file.getContentType());
        img.setSize(file.getSize());
        img.setData(file.getBytes());

        return imageRepository.save(img);
    }

    @Override
    public ProductImage getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));
    }

    @Override
    public void deleteImage(Long imageId, User currentUser) {
        ProductImage img = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));

        // Seguridad: solo el dueño del producto puede eliminar imágenes
        if (!img.getProduct().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para eliminar este producto");
        }

        imageRepository.deleteById(imageId);
    }

    @Override
    public List<ProductImage> listByProduct(Long productId) {
        return imageRepository.findByProductId(productId);
    }
}
