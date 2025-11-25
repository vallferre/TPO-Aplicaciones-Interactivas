package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.CategoryImage;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    
    CategoryImage findByCategoryId(Long categoryId);
}