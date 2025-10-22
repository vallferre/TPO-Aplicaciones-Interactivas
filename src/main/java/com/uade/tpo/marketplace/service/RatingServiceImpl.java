package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.Rating;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.RatingResponse;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.RatingRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public double getAverageRatingForProduct(Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);
        if (ratings.isEmpty()) return 5.0; // valor por defecto si no hay ratings
        double sum = ratings.stream().mapToDouble(Rating::getValue).sum();
        return sum / ratings.size();

    }

    @Override
    public int getNumberOfRatingsForProduct(Long productId) {
        return ratingRepository.findNumberOfRatings(productId);
    }

    @Override
    public int countRatingsByValueForProduct(Long productId, int value) {
        return ratingRepository.countByProductAndValue(productId, value);
    }

    @Override
    public Rating addRatingToProduct(Long productId, Long userId, int value, String comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Opcional: validar si ya existe rating de este user para este producto
        Rating existing = ratingRepository.findByProductIdAndUserId(productId, userId);
        if (existing != null) {
            existing.setValue(value);
            existing.setComment(comment);
            return ratingRepository.save(existing);
        }

        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setValue(value);
        rating.setComment(comment);

        return ratingRepository.save(rating);
    }

    @Override
    public List<RatingResponse> getRatingsForProduct(Long productId) {
        // Obtener todos los ratings del producto
        List<Rating> ratings = ratingRepository.findByProductId(productId);

        // Mapear a RatingResponse
        return ratings.stream()
                .map(RatingResponse::from)
                .collect(Collectors.toList());
    }

}