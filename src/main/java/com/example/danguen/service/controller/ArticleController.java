package com.example.danguen.service.controller;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping(value = "/article", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void save(@RequestPart("request") RequestArticleSaveOrUpdateDto request,
                     @RequestPart("images") List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {

        log.info("request = {}", request);
        log.info("images = {}", images);
        log.info("request = {}", request.getTitle());

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
    public ModelAndView getArticle(@PathVariable Long articleId) {
        ModelAndView mav = new ModelAndView("articlePage");
        mav.addObject("article", articleService.getArticle(articleId));

        return mav;
    }

    @GetMapping("/address/**")
    public ModelAndView getArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                         HttpServletRequest servletRequest) {
        List<ResponseArticleSimpleDto> articles = articleService.getArticlePage(pageable,
                new Address(
                        (String) servletRequest.getAttribute("city"),
                        (String) servletRequest.getAttribute("street"),
                        (String) servletRequest.getAttribute("zipcode")
                )
        );

        ModelAndView mav = new ModelAndView("articleList");
        mav.addObject("articles", articles);

        return mav;
    }

    @GetMapping("/hot-articles")
    public ModelAndView getHotArticlePage(@PageableDefault(size = 6) Pageable pageable) {
        List<ResponseArticleSimpleDto> hArticles = articleService.getHotArticlePage(pageable);

        ModelAndView mav = new ModelAndView("articleList");
        mav.addObject("hArticle", hArticles);

        return mav;
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
