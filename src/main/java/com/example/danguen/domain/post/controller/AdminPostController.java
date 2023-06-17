package com.example.danguen.domain.post.controller;

import com.example.danguen.domain.image.service.PostImageService;
import com.example.danguen.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/post")
@RequiredArgsConstructor
@RestController
public class AdminPostController {
    @Value("${file.article.image.path}")
    private String savePath;

    private final PostImageService postImageService;
    private final PostService postService;

    @DeleteMapping("/*/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
        postImageService.deleteFolder(savePath + postId);
    }
}
