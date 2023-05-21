package com.example.danguen.domain.comment.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.exception.CommentNotFoundException;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment") // 댓글이 달릴 객체 필요
    public void save(@RequestBody RequestCommentSaveDto requestCommentSaveDto,
                     @PathVariable Long postId,
                     @SessionUserId Long userId) {

        commentService.save(requestCommentSaveDto, postId, userId);
    }

    @PutMapping("/comment/{commentId}")
    public void update(@RequestBody RequestCommentSaveDto requestCommentSaveDto,
                       @PathVariable Long commentId) {
        commentService.update(requestCommentSaveDto, commentId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    @PostMapping("/comment/{commentId}/like")
    public int like(@PathVariable Long commentId,
                    @SessionUserId Long userId) {
        return commentService.like(commentId, userId);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public String handleCommentNotFound() {
        return CommentNotFoundException.message;
    }

    @ExceptionHandler(AlreadyDeletedCommentException.class)
    public String handleArticleDeleted() {
        return AlreadyDeletedCommentException.message;
    }
}
