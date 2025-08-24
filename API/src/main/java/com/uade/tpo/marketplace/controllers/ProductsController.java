package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.service.ProductService;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("products")
public class ProductsController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //Obtener todos los productos (con paginación opcional)
    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productService.getProducts(PageRequest.of(page, size)));
    }

    //Obtener producto por ID
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Optional<Product> result = productService.getProductById(productId);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    //Crear producto
    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody ProductRequest productRequest)
            throws ProductDuplicateException {
        Optional<Category> categoryOpt = categoryRepository.findById(productRequest.getCategoryId());
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Categoría no encontrada");
        }

        Product newProduct = new Product();
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setStock(productRequest.getStock());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setCategory(categoryOpt.get());

        Product result = productService.createProduct(newProduct);
        return ResponseEntity.created(URI.create("/products/" + result.getId())).body(result);
    }

    //Eliminar producto
    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long productId) throws ProductNotFoundException {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    //Actualizar stock
    @PutMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long productId,
            @RequestParam int stock) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.updateStock(productId, stock));
    }

    //Aplicar descuento
    @PutMapping("/{productId}/discount")
    public ResponseEntity<Product> applyDiscount(
            @PathVariable Long productId,
            @RequestParam double percentage) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.applyDiscount(productId, percentage));
    }

    // ================
    // ENDPOINTS EXTRA 
    // ================

    // Productos sin stock
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<Product>> getOutOfStock() {
        return ResponseEntity.ok(productRepository.findOutOfStock());
    }

    // Productos con precio menor a X
    @GetMapping("/cheaper-than")
    public ResponseEntity<List<Product>> getProductsCheaperThan(@RequestParam double price) {
        return ResponseEntity.ok(productRepository.findByPriceLessThan(price));
    }

    // Productos con precio mayor a X
    @GetMapping("/expensive-than")
    public ResponseEntity<List<Product>> getProductsMoreExpensiveThan(@RequestParam double price) {
        return ResponseEntity.ok(productRepository.findByPriceGreaterThan(price));
    }

    // Buscar por palabra clave en la descripción
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productRepository.findByDescriptionContainingIgnoreCase(keyword));
    }

    // Productos ordenados por precio ascendente
    @GetMapping("/order-by-price-asc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceAsc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByPriceAsc());
    }

    // Productos ordenados por precio descendente
    @GetMapping("/order-by-price-desc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceDesc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByPriceDesc());
    }

    // Productos ordenados por stock
    @GetMapping("/order-by-stock")
    public ResponseEntity<List<Product>> getProductsOrderByStockDesc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByStockDesc());
    }
}
