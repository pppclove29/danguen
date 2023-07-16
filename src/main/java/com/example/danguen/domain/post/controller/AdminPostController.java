package com.example.danguen.domain.post.controller;

import com.example.danguen.domain.image.service.PostImageService;
import com.example.danguen.domain.post.service.PostServiceImpl;
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

    private final PostImageService postImageService;
    private final PostServiceImpl postServiceImpl;

    @DeleteMapping("/*/{postId}")
    public void delete(@PathVariable Long postId) {
        postServiceImpl.delete(postId);
        postImageService.delete(postId);
    }
}
