package com.example.danguen.domain.post.controller.free;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.image.service.PostImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/secured235234")
@RequiredArgsConstructor
@RestController
public class SecuredFreeController {
    @Value("${file.article.image.path}")
    private String savePath;
    private final PostImageService postImageService;
    private final PostService postService;
    private final ArticleServiceImpl articleService;

    @PostMapping("/article")
    public void save(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                     @RequestParam(value = "images") List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
        Long articleId = articleService.save(request, userId);
        postImageService.save(articleId, images);
    }

    @PutMapping("/article/{articleId}")
    public void update(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                       @PathVariable Long articleId,
                       @RequestParam(value = "images") List<MultipartFile> images) {
        articleService.update(request, articleId);
        postImageService.update(articleId, images);
    }

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId) {
        postService.delete(articleId);
        postImageService.deleteFolder(savePath + articleId);
    }

    @PostMapping("/article/{articleId}/interest")
    public void giveInterestToArticle(@PathVariable Long articleId,
                                      @SessionUserId Long userId) {
        articleService.giveInterest(articleId, userId);
    }


}
