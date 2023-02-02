package com.example.danguen.service.api;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.CommentNotFoundException;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{post}/{postId}/comment") // 댓글이 달릴 객체 필요
    public void save(@RequestBody RequestCommentSaveDto request,
                     @PathVariable String post,
                     @PathVariable Long postId,
                     @SessionUserId Long userId) {

        commentService.save(request, post, postId, userId);
    }

    @PutMapping("/comment/{commentId}")
    public void update(@RequestBody RequestCommentSaveDto request,
                       @PathVariable Long commentId) {
        commentService.update(request, commentId);
    }

    @DeleteMapping("/comment/{commentId}") // 댓글이 달린 객체 필요, 이미 저장되어있으니 내부의
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    @PostMapping("/comment/{commentId}/like")
    public void like(@PathVariable Long commentId) {
        commentService.like(commentId);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public String handleCommentNotFound() {
        return "commentNotFound";
    }
}
