package com.example.danguen.domain.post.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.image.repository.ArticleImageRepository;
import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/secured")
@RequiredArgsConstructor
@RestController
public class SecuredArticleController {
    @Value("${file.article.image.path}")
    private String savePath;
    private final ArticleImageService articleImageService;
    private final ArticleServiceImpl articleService;

    @GetMapping("/test")
    public String secureTest() {
        return "secureTest";
    }

    @PostMapping(value = "/article")
    public void save(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                     @RequestParam(value = "images") List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
        Long articleId = articleService.save(request, userId);
        articleImageService.save(articleId, images);
    }

    @PutMapping("/article/{articleId}")
    public void update(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                       @PathVariable Long articleId,
                       @RequestParam(value = "images") List<MultipartFile> images) {
        articleService.update(request, articleId);
        articleImageService.update(articleId, images);
    }

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
        articleImageService.deleteFolder(savePath + articleId);
    }

    @PostMapping("/article/{articleId}/interest")
    public void giveInterestToArticle(@PathVariable Long articleId,
                                      @SessionUserId Long userId) {
        articleService.giveInterest(articleId, userId);
    }


}
