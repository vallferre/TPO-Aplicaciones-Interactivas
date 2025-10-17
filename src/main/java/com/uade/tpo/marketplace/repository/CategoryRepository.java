package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.CategoryImage;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    @Query(value = "select c from Category c where lower(c.description) = lower(:description)")   
    Category findByDescription(@Param("description") String description);

    List<Category> findByDescriptionIn(List<String> description); //.

    // Buscar la categoría por el ID de la imagen asociada
    @Query("SELECT c.fileImage FROM Category c WHERE c.fileImage.id = :imageId")
    CategoryImage findImageByFileImageId(@Param("imageId") Long imageId);

}
