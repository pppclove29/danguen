package com.example.danguen.service.api;

import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public void register(@RequestParam RequestCommentSaveDto request) {
        commentService.register(request);
    }

    public void update() {
        commentService.update();
    }

    public void delete() {
        commentService.delete();
    }

    public void like() {
        commentService.like();
    }
}
