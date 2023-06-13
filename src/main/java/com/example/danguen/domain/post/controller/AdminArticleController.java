package com.example.danguen.domain.post.controller;


import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminArticleController {
    @Value("${file.article.image.path}")
    private String savePath;

    private final ArticleImageService articleImageService;
    private final ArticleServiceImpl articleService;

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
        articleImageService.deleteFolder(savePath + articleId);
    }
}
