package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.marketplace.entity.CategoryImage;

public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    List<CategoryImage> findByCategoryId(Long categoryId);
}
