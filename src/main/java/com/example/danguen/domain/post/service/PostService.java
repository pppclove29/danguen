package com.example.danguen.domain.post.service;

import com.example.danguen.domain.post.dto.request.RequestPostSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponsePostDto;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;

public interface PostService {
    ResponsePostDto getPostDto(Long postId);

    void giveLike(Long postId, Long userId);

    Long save(RequestPostSaveOrUpdateDto request, Long userId, PostKind.Kind kind);

    void update(RequestPostSaveOrUpdateDto request, Long postId);

    void delete(Long postId);

    Post getPostById(Long postId);
}
