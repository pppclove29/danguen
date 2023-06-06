package com.example.danguen.domain.post.service;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
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

    private final ArticlePostRepository articlePostRepository;


    @Override
    @Transactional
    public ResponseArticleDto getArticleDto(Long articleId) {
        ArticlePost articlePost = getArticleById(articleId);
        articlePost.addViewCount();

        return ResponseArticleDto.toResponse(articlePost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getArticleByAddressPage(Pageable pageable, Address address) {
        System.out.println(address.toString());
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
    @Transactional(readOnly = true)
    public List<ResponseArticleSimpleDto> getInterestPage(Pageable pageable, Long userId) {
        List<User> interestUser = userService.getUserById(userId).getInterestUsers();

        Page<ArticlePost> page = articlePostRepository.findByInterestUser(pageable, interestUser);

        return page.stream().map(ResponseArticleSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long save(RequestArticleSaveOrUpdateDto request, Long userId) throws IOException {
        ArticlePost articlePost = request.toEntity();

        User user = userService.getUserById(userId);
        user.addSellArticle(articlePost);

        return articlePostRepository.save(articlePost).getId();
    }

    @Override
    @Transactional
    public void update(RequestArticleSaveOrUpdateDto request, Long articleId) {
        ArticlePost articlePost = getArticleById(articleId);

        articlePost.updateArticle(request);
    }

    @Override
    @Transactional
    public void delete(Long articleId) {
        ArticlePost articlePost = getArticleById(articleId);
        User user = articlePost.getSeller();

        user.removeSellArticle(articlePost);
        for (Comment comment : articlePost.getComments())
            comment.getWriter().removeComment(comment);

        articlePostRepository.deleteById(articleId);
    }

    @Override
    public ArticlePost getArticleById(Long articleId) {
        return articlePostRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
    }
}
