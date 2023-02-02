package com.example.danguen.service.service;

import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public void register(RequestCommentSaveDto request) {

    }

    public void update(RequestCommentSaveDto request, Long commentId) {

    }

    public void delete(Long commentId) {

    }

    public void like(Long commentId) {

    }
}
