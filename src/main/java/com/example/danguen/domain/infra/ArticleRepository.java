package com.example.danguen.domain.infra;

import com.example.danguen.domain.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    Page<Article> findAllByTitleLikeKeywordOrContentLikeKeyword(Pageable pageable, String keyword);

}
