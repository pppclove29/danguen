package com.example.danguen.service.api;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.image.dto.ImageDto;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.service.service.ArticleService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

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
        return articleService.getArticle(articleId);
    }

    @GetMapping("/address/**")
    public List<ResponseArticleSimpleDto> getArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                         HttpServletRequest servletRequest) {
        return articleService.getArticlePage(pageable,
                new Address(
                        (String) servletRequest.getAttribute("city"),
                        (String) servletRequest.getAttribute("street"),
                        (String) servletRequest.getAttribute("zipcode")
                )
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
