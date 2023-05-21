package com.example.danguen.domain.post.repository;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticlePostRepository extends JpaRepository<ArticlePost, Long> {

    Page<ArticlePost> findAllByDealHopeAddressLikeOrderByCreatedTimeDesc(Pageable pageable, @Param("address") Address address);

    Page<ArticlePost> findByTitleContainingOrderByIdDesc(Pageable pageable, String title);

    @Query("select a from ArticlePost a where a.seller in :interestUser")
    Page<ArticlePost> findByInterestUser(Pageable pageable, @Param("interestUser") List<User> interestUser);

    // 일단은 조회수로만 하자, 관심등록 및 댓글 수는 후에 추가한다, 단기간 조회수 상승률은 통계영역이므로 일단 보류한다
    @Query("select a from ArticlePost a order by a.views desc")
    Page<ArticlePost> findByHot(Pageable pageable);

}