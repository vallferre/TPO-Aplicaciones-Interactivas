package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.marketplace.entity.UserImage;

public interface UserImageRepository extends JpaRepository<UserImage, Long>{
    UserImage findByUserId(Long userId);
}
