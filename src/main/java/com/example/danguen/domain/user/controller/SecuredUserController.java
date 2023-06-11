package com.example.danguen.domain.user.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/secured")
@RequiredArgsConstructor
@RestController
public class SecuredUserController {

    private final UserServiceImpl userService;
    private final ArticleServiceImpl articleService;

    @GetMapping("/user/{userId}")
    public ResponseUserPageDto getInfo(@PathVariable Long userId) {
        return userService.getUserDto(userId);
    }

    @PutMapping("/user")
    public void update(@RequestBody RequestUserUpdateDto request,
                       @SessionUserId Long userId) {
        userService.update(request, userId);
    }

    @DeleteMapping("/user")
    public void delete(@SessionUserId Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/user/{otherUserId}/review")
    public void reviewSeller(@RequestBody RequestReviewDto request,
                             @PathVariable Long otherUserId) {
        userService.review(request, otherUserId);
    }

    @GetMapping("/user/iuser")
    public List<ResponseUserSimpleDto> getIUsers(@SessionUserId Long userId) {
        return userService.getIUserDtos(userId);
    }

    @PutMapping("/user/iuser/{iUserId}")
    public void addInterestUser(@PathVariable Long iUserId,
                                @SessionUserId Long userId) {
        userService.addInterestUser(userId, iUserId);
    }

    @DeleteMapping("/user/iuser/{iUserId}")
    public void deleteInterestUser(@PathVariable Long iUserId,
                                   @SessionUserId Long userId) {
        userService.deleteInterestUser(userId, iUserId);
    }

    @GetMapping("/user/iarticle")
    public List<ResponseArticleSimpleDto> getInterestArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                                 @SessionUserId Long userId) {
        return articleService.getInterestArticlePage(pageable, userId);
    }

    @GetMapping("/user/iusers/articles")
    public List<ResponseArticleSimpleDto> getInterestUsersArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                                      @SessionUserId Long userId) {
        return articleService.getInterestUsersArticlePage(pageable, userId);
    }


}
