package com.example.danguen.domain.post.controller.notice;

import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.post.dto.response.ResponsePostDto;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public/post")
@RequiredArgsConstructor
@RestController
public class PublicNoticeController {

    private final PostServiceImpl postService;
    private final CommentService commentService;

    @GetMapping("/notice/{noticeId}")
    public ResponsePostDto getNotice(@PathVariable Long noticeId) {
        ResponsePostDto post = postService.getPostDto(noticeId);
        post.addComments(commentService.getComments(noticeId));

        return post;
    }
}
