package com.example.danguen.domain.post.service;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.Article;
import com.example.danguen.domain.post.repository.ArticleRepository;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;

    private final ArticleRepository articleRepository;


    @Override
    @Transactional(readOnly = true)
    public ResponseArticleDto getArticle(Long articleId) {
        Article article = getArticleFromDB(articleId);
        article.addViewCount();

        return ResponseArticleDto.toResponse(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getArticleByAddressPage(Pageable pageable, Address address) {
        Page<Article> page =
                articleRepository.findAllByDealHopeAddressLikeOrderByCreatedTimeDesc(pageable, address);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getHotArticlePage(Pageable pageable) {
        Page<Article> page = articleRepository.findByHot(pageable);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getSearchArticlePage(Pageable pageable, String title) {
        Page<Article> page = articleRepository.findByTitleContainingOrderByIdDesc(pageable, title);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getInterestPage(Pageable pageable, Long userId) {
        List<User> interestUser = userService.getUserFromDB(userId).getInterestUser();

        Page<Article> page = articleRepository.findByInterestUser(pageable, interestUser);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long save(RequestArticleSaveOrUpdateDto request, Long userId) throws IOException {
        Article article = request.toEntity();

        User user = userService.getUserFromDB(userId);
        user.addSellArticle(article);

        return articleRepository.save(article).getId();
    }

    @Override
    @Transactional
    public void update(RequestArticleSaveOrUpdateDto request, Long articleId) {
        Article article = getArticleFromDB(articleId);

        article.updateArticle(request);
    }

    @Override
    @Transactional
    public void delete(Long articleId) {
        Article article = getArticleFromDB(articleId);
        User user = article.getSeller();

        user.removeSellArticle(article);
        for (Comment comment : article.getComments())
            comment.getWriter().removeComment(comment);

        articleRepository.deleteById(articleId);
    }

    @Override
    public Article getArticleFromDB(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
    }
}
