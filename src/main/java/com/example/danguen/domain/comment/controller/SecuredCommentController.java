package com.example.danguen.domain.comment.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.exception.CommentNotFoundException;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.comment.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/secured")
@RequiredArgsConstructor
@RestController
public class SecuredCommentController {

    private final CommentServiceImpl commentService;
    private final CommentRepository commentRepository;

    @PostMapping("/post/{postId}/comment") // 댓글이 달릴 객체 필요
    public void saveInPost(@RequestBody RequestCommentSaveDto requestCommentSaveDto,
                           @PathVariable Long postId,
                           @SessionUserId Long userId) {
        commentService.saveInPost(requestCommentSaveDto, postId, userId);
    }

    @PostMapping("/comment/{commentId}/comment")
    public void saveInComment(@RequestBody RequestCommentSaveDto requestCommentSaveDto,
                              @PathVariable Long commentId,
                              @SessionUserId Long userId) {
        commentService.saveInComment(requestCommentSaveDto, commentId, userId);
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

    @ExceptionHandler(AlreadyDeletedCommentException.class)
    public ResponseEntity<?> handleCommentAlreadyDeleted() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AlreadyDeletedCommentException.message);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<?> handleCommentNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommentNotFoundException.message);
    }
}
