package com.example.danguen.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.exception.PostNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;

    private final ArticlePostRepository articlePostRepository;


    @Override
    public ResponseArticleDto getArticleDto(Long articleId) {
        ArticlePost articlePost = getArticleById(articleId);
        articlePost.addViewCount();

        return ResponseArticleDto.toResponse(articlePost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getArticleByAddressPage(Pageable pageable, Address address) {
        Page<ArticlePost> page =
                articlePostRepository.findAllByDealHopeAddressLikeOrderByCreatedTimeDesc(
                        address.getCity(),
                        address.getStreet(),
                        address.getZipcode(),
                        pageable
                );

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getHotArticlePage(Pageable pageable) {
        Page<ArticlePost> page = articlePostRepository.findByHot(pageable);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getSearchArticlePage(Pageable pageable, String title) {
        Page<ArticlePost> page = articlePostRepository.findByTitleContainingOrderByIdDesc(pageable, title);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    public void giveInterest(Long articleId, Long userId) {
        ArticlePost articlePost = getArticleById(articleId);
        User user = userService.getUserById(userId);

        articlePost.addInterest(user);
    }
    //todo removeInterest

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getInterestArticlePage(Pageable pageable, Long userId) {
        List<ArticlePost> interestArticles = userService.getUserById(userId).getInterestArticles();

        Page<ArticlePost> page = articlePostRepository.findByInterestArticles(pageable, interestArticles);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getInterestUsersArticlePage(Pageable pageable, Long userId) {
        List<User> interestUser = userService.getUserById(userId).getInterestUsers();

        Page<ArticlePost> page = articlePostRepository.findByInterestUsersArticle(pageable, interestUser);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    public Long save(RequestArticleSaveOrUpdateDto request, Long userId) {
        ArticlePost articlePost = request.toEntity();

        User user = userService.getUserById(userId);
        user.addSellArticle(articlePost);

        ArticlePost savedArticle = articlePostRepository.save(articlePost);

        return savedArticle.getId();
    }

    @Override
    public void update(RequestArticleSaveOrUpdateDto request, Long articleId) {
        ArticlePost articlePost = getArticleById(articleId);

        articlePost.updateArticle(request);
    }

    @Override
    public ArticlePost getArticleById(Long articleId) {
        return articlePostRepository.findById(articleId).orElseThrow(PostNotFoundException::new);
    }


}
