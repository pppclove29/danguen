package com.example.danguen.domain.post.controller.notice;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.dto.response.ResponsePostDto;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
