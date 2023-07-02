package com.example.danguen.domain.post.controller.free;

import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.post.dto.response.ResponsePostDto;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/public/post")
@RequiredArgsConstructor
@RestController
public class PublicFreeController {

    private final PostServiceImpl postService;
    private final CommentService commentService;

    @GetMapping("/free/{freeId}")
    public ResponsePostDto getArticle(@PathVariable Long freeId) {
        ResponsePostDto post = postService.getPostDto(freeId);
        List<ResponseCommentDto> commentDtoList = commentService.getComments(freeId);

        post.addComments(commentDtoList);

        return post;
    }
}
