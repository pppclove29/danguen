package com.example.danguen.domain.comment.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.comment.entity.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.exception.CommentNotFoundException;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postName}/{postId}/comment") // 댓글이 달릴 객체 필요
    public ResponseEntity<?> save(@RequestParam String content,
                                  @PathVariable String postName,
                                  @PathVariable Long postId,
                                  @SessionUserId Long userId,
                                  HttpServletRequest httpRequest) {

        commentService.save(new RequestCommentSaveDto(content), postName, postId, userId);

        HttpHeaders headers = new HttpHeaders();

        String uri = httpRequest.getRequestURI().replace("/comment", "");
        headers.setLocation(URI.create(uri));

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY); // 게시글 경로로 다시 이동
    }

    @GetMapping("/{postName}/{postId}/comment")
    public Stream<ResponseCommentDto> getComments(@PathVariable String postName,
                                                  @PathVariable Long postId) {
        return commentService.getComments(postName, postId);
    }

    @PutMapping("/comment/{commentId}")
    public void update(@RequestParam String content,
                       @PathVariable Long commentId) {
        commentService.update(new RequestCommentSaveDto(content), commentId);
    }

    @DeleteMapping("/comment/{commentId}") // 댓글이 달린 객체 필요, 이미 저장되어있으니 내부의
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
