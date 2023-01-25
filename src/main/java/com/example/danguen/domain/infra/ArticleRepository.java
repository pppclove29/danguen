package com.example.danguen.domain.infra;

import com.example.danguen.domain.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    Page<Article> findByTitleContaining(Pageable pageable, String title);

    @Query("select a from Article a order by a.views desc") // 일단은 조회수로만 하자, 관심등록 및 댓글 수는 후에 추가한다, 단기간 조회수 상승률은 통계영역이므로 일단 보류한다
    Page<Article> findByHot(Pageable pageable);
}
