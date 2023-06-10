package com.example.danguen.domain.comment.service;

import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment saveInPost(RequestCommentSaveDto request, Long postId, Long userId);

    Comment saveInComment(RequestCommentSaveDto request, Long commentId, Long userId);

    List<ResponseCommentDto> getComments(Long postId);

    void update(RequestCommentSaveDto request, Long commentId);

    void delete(Long commentId);

    int like(Long commentId, Long userId);

    Comment getCommentById(Long commentId);
}
