package com.example.danguen.domain.post.service;

import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {
    //TODO 인터페이스 구현
    private final PostRepository postRepository;

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(ArticleNotFoundException::new);
    }

    public void delete(Long postId) {
        postRepository.deleteById(postId);
    }
}
