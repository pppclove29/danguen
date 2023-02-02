package com.example.danguen.service.api;

import com.example.danguen.config.exception.CommentNotFoundException;
import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public void register(@RequestBody RequestCommentSaveDto request) {
        commentService.register(request);
    }

    @PutMapping("/comment/{commentId}")
    public void update(@RequestBody RequestCommentSaveDto request,
                       @PathVariable Long commentId) {
        commentService.update(request, commentId);
    }

    @DeleteMapping("/comment/{commentId}")
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
