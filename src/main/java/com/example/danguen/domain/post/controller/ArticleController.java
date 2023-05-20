package com.example.danguen.domain.post.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    @PostMapping(value = "/article", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void save(@RequestPart("request") RequestArticleSaveOrUpdateDto request,
                     @RequestPart("images") List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
        articleService.save(request, userId, images);
    }

    @PutMapping("/article/{articleId}")
    public void update(@RequestBody RequestArticleSaveOrUpdateDto request,
                       @PathVariable Long articleId) {
        articleService.update(request, articleId);
    }

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }


    @GetMapping("/article/{articleId}")
    public ResponseArticleDto getArticle(@PathVariable Long articleId) {
        ResponseArticleDto post = articleService.getArticle(articleId);
        Stream<ResponseCommentDto> commentDtoStream = commentService.getComments("article", articleId);

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
