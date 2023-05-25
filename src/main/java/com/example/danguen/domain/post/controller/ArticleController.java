package com.example.danguen.domain.post.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleServiceImpl articleService;
    private final ArticleImageService articleImageService;
    private final CommentService commentService;


    //todo 권한별 역할 나눌것
    @PostMapping(value = "/article")
    public void save(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                     @RequestParam(value = "images", required = false) List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
        Long articleId = articleService.save(request, userId);
        articleImageService.save(articleId, images);
    }

    @PutMapping("/article/{articleId}")
    public void update(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                       @PathVariable Long articleId,
                       @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        articleService.update(request, articleId);
        articleImageService.update(images);
    }

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }


    @GetMapping("/article/{articleId}")
    public ResponseArticleDto getArticle(@PathVariable Long articleId) {
        ResponseArticleDto post = articleService.getArticleDto(articleId);
        List<ResponseCommentDto> commentDtoStream
                = commentService.getComments(articleId);

        post.addComments(commentDtoStream);

        return post;
    }

    @GetMapping("/address/**")
    public List<ResponseArticleSimpleDto> getArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                         HttpServletRequest servletRequest) {
        return articleService.getArticleByAddressPage(
                pageable, (Address) servletRequest.getAttribute("address")
        );
    }

    @GetMapping("/hot-articles")
    public List<ResponseArticleSimpleDto> getHotArticlePage(@PageableDefault(size = 6) Pageable pageable) {
        return articleService.getHotArticlePage(pageable);
    }

    @GetMapping("/search")
    public List<ResponseArticleSimpleDto> getSearchPage(@PageableDefault(size = 6) Pageable pageable,
                                                        @RequestParam("keyword") String title) {
        return articleService.getSearchArticlePage(pageable, title);
    }

    @GetMapping("/interest")
    public List<ResponseArticleSimpleDto> getInterestPage(@PageableDefault(size = 6) Pageable pageable,
                                                          @SessionUserId Long userId) {
        return articleService.getInterestPage(pageable, userId);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public String handleArticleNotFound() {
        return ArticleNotFoundException.message;
    }
}
