package com.uade.tpo.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.Rating;
import com.uade.tpo.marketplace.entity.User;
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
        Double avg = ratingRepository.findAverageRating(productId);
        return avg != null ? avg : 0.0;

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

}