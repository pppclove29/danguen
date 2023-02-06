package com.example.danguen.domain.repository;

import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("select a from Article a where " +
            "a.dealHopeAddress.city like %:city% and " +
            "a.dealHopeAddress.street like %:street% and " +
            "a.dealHopeAddress.zipcode like %:zipcode% " +
            "order by a.createdTime desc ")
    Page<Article> findAllByAddress(Pageable pageable,
                                   @Param("city") String city,
                                   @Param("street") String street,
                                   @Param("zipcode") String zipcode);

    Page<Article> findByTitleContainingOrderByIdDesc(Pageable pageable, String title);

    @Query("select a from Article a where a.seller in :interestUser")
    Page<Article> findByInterestUser(Pageable pageable, @Param("interestUser")List<User> interestUser);

    @Query("select a from Article a order by a.views desc")
        // 일단은 조회수로만 하자, 관심등록 및 댓글 수는 후에 추가한다, 단기간 조회수 상승률은 통계영역이므로 일단 보류한다
    Page<Article> findByHot(Pageable pageable);

}