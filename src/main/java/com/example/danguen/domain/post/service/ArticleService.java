package com.example.danguen.domain.post.service;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    ResponseArticleDto getArticleDto(Long articleId);

    List<ResponseArticleSimpleDto> getArticleByAddressPage(Pageable pageable, Address address);

    List<ResponseArticleSimpleDto> getHotArticlePage(Pageable pageable);

    List<ResponseArticleSimpleDto> getSearchArticlePage(Pageable pageable, String title);

    void giveInterest(Long postId, Long userId);

    List<ResponseArticleSimpleDto> getInterestArticlePage(Pageable pageable, Long userId);

    List<ResponseArticleSimpleDto> getInterestUsersArticlePage(Pageable pageable, Long userId);

    Long save(RequestArticleSaveOrUpdateDto request, Long userId);

    void update(RequestArticleSaveOrUpdateDto request, Long articleId);

    ArticlePost getArticleById(Long articleId);
}
