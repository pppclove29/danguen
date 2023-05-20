package com.example.danguen.domain.image.repository;

import com.example.danguen.domain.image.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
}
