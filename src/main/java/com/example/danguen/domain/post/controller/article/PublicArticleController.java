package com.example.danguen.domain.post.controller.article;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.service.CommentService;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.dto.response.ResponsePostSimpleDto;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/public/post")
@RequiredArgsConstructor
@RestController
public class PublicArticleController {

    private final ArticleServiceImpl articleService;
    private final PostServiceImpl postService;
    private final CommentService commentService;

    @GetMapping("/test")
    public Temp test(@PageableDefault(size = 6) Pageable pageable) {
        Temp t = new Temp();
        t.articleList = articleService.getHotArticlePage(pageable);
        t.noticeList = postService.getNotices();

        return t;
    }

    @GetMapping("/article/{articleId}")
    public ResponseArticleDto getArticle(@PathVariable Long articleId) {
        ResponseArticleDto post = articleService.getArticleDto(articleId);
        post.addComments(commentService.getComments(articleId));

        return post;
    }

    @GetMapping(value = {
            "/address",
            "/address/{city}",
            "/address/{city}/{street}",
            "/address/{city}/{street}/{zipcode}"})
    public List<ResponseArticleSimpleDto> getArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                         @PathVariable(required = false) String city,
                                                         @PathVariable(required = false) String street,
                                                         @PathVariable(required = false) String zipcode) {
        return articleService.getArticleByAddressPage(
                pageable, new Address(city, street, zipcode)
        );
    }

    //todo WithMockCustomUser수, 좋아요, 댓글 수, 채팅 수에 점수 메기고 그에 따라 정렬
    @GetMapping("/hot-articles")
    public List<ResponseArticleSimpleDto> getHotArticlePage(@PageableDefault(size = 6) Pageable pageable) {
        return articleService.getHotArticlePage(pageable);
    }

    @GetMapping("/search")
    public List<ResponseArticleSimpleDto> getSearchPage(@PageableDefault(size = 6) Pageable pageable,
                                                        @RequestParam("keyword") String title) {
        return articleService.getSearchArticlePage(pageable, title);
    }
}

@Getter
class Temp implements Serializable{
    List<ResponseArticleSimpleDto> articleList = new ArrayList<>();
    List<ResponsePostSimpleDto> noticeList = new ArrayList<>();
}
