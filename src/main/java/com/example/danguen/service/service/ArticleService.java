package com.example.danguen.service.service;

import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public ResponseArticleDto getArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
        article.addViewCount();

        return ResponseArticleDto.toResponse(article);
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getArticlePage(Pageable pageable, Address address) {
        Page<Article> page = articleRepository.findAllByAddress(pageable, address.getCity(), address.getStreet(), address.getZipcode());

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getHotArticlePage(Pageable pageable) {
        Page<Article> page = articleRepository.findByHot(pageable);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getSearchArticlePage(Pageable pageable, String title) {
        Page<Article> page = articleRepository.findByTitleContainingOrderByIdDesc(pageable, title);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getInterestPage(Pageable pageable, Long userId) {
        List<User> interestUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getInterestUser();

        Page<Article> page = articleRepository.findByInterestUser(pageable, interestUser);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void save(RequestArticleSaveOrUpdateDto request, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Article article = request.toEntity();

        user.addSellArticle(article);

        articleRepository.save(article);
    }

    @Transactional
    public void update(RequestArticleSaveOrUpdateDto request, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        article.updateArticle(request);
    }

    @Transactional
    public void delete(Long articleId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        user.removeSellArticle(article);

        articleRepository.deleteById(articleId);
    }
}
