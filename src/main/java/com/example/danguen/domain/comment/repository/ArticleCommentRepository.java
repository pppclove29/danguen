package com.example.danguen.domain.comment.repository;

import com.example.danguen.domain.comment.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findAllByArticle_Id(Long articleId);
}
