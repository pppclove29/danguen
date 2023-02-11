package com.example.danguen.service.service;

import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.image.ArticleImage;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.UserRepository;
import com.example.danguen.domain.repository.image.ArticleImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleService {
    @Value("${file.article.image.path}")
    private String savePath;

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;


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
    public void save(RequestArticleSaveOrUpdateDto request, Long userId, List<MultipartFile> images) throws IOException {
        Article article = request.toEntity();

        User user = userRepository.getReferenceById(userId);
        user.addSellArticle(article);

        articleRepository.save(article);

        File file = new File(savePath + article.getId());
        file.mkdirs();

        int imageIdx = 1;
        for (MultipartFile image : images) {
            String imageName = image.getOriginalFilename();

            ArticleImage articleImage = ArticleImage.builder()
                    .name(imageName)
                    .url(savePath + article.getId() + "/" + imageIdx + ".jpg")
                    .article(article)
                    .build();

            //로컬에 이미지 저장
            image.transferTo(new File(articleImage.getUrl()));

            articleImageRepository.save(articleImage);
        }
    }

    @Transactional
    public void update(RequestArticleSaveOrUpdateDto request, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        article.updateArticle(request);
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
        User user = article.getSeller();

        user.removeSellArticle(article);
        for (Comment comment : article.getComments())
            comment.getWriter().removeComment(comment);

        articleRepository.deleteById(articleId);
    }
}
