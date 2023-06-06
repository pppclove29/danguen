package com.example.danguen.domain.image.repository;

import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
    List<ArticleImage> findByArticlePost(ArticlePost articlePost);

    void deleteArticleImageByArticlePost(ArticlePost articlePost);
}
