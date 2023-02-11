package com.example.danguen.service.controller;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseUserPageDto getInfo(@PathVariable Long userId) {
        return userService.getUserPage(userId);
    }

    @PutMapping("/user/{userId}")
    public ResponseUserPageDto update(@RequestBody RequestUserUpdateDto request,
                                      @PathVariable Long userId) {
        return userService.update(request, userId);
    }

    @DeleteMapping("/user/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/user/{userId}/review-seller")
    public void reviewSeller(@RequestBody RequestSellerReviewDto request,
                             @PathVariable Long userId) {
        userService.reviewSeller(request, userId);
    }

    @PostMapping("/user/{userId}/review-buyer")
    public void reviewBuyer(@RequestBody RequestBuyerReviewDto request,
                            @PathVariable Long userId) {
        userService.reviewBuyer(request, userId);
    }

    @GetMapping("/user/iuser")
    public List<ResponseUserSimpleDto> getIUsers(@SessionUserId Long userId) {
        return userService.getIUsers(userId);
    }

    @PutMapping("/user/iuser/{iuserId}")
    public void addInterestUser(@PathVariable Long iuserId) {
        userService.addInterestUser(iuserId);
    }

    @DeleteMapping("/user/iuser/{iuserId}")
    public void deleteInterestUser(@PathVariable Long iuserId) {
        userService.deleteInterestUser(iuserId);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound() {
        return UserNotFoundException.message;
    }
}
