package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    @Query(value = "select c from Category c where c.description = :description")
    List<Category> findByDescription(@Param("description") String description);


}
