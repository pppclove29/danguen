package com.example.danguen.domain.repository.comment;

import com.example.danguen.domain.model.comment.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findAllByArticle_Id(Long articleId);
}
