package com.example.danguen.service.api;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

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

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound() {
        return "userNotFound";
    }
}
