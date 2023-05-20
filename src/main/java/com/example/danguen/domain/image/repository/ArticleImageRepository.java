package com.example.danguen.domain.image.repository;

import com.example.danguen.domain.image.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
}
