package com.example.danguen.domain.repository;

import com.example.danguen.domain.model.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
