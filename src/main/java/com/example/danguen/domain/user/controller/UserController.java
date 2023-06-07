package com.example.danguen.domain.user.controller;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/test")
    public Long test(@SessionUserId Long userId) {
        return userId;
    }

    @GetMapping("/user/{userId}")
    public ResponseUserPageDto getInfo(@PathVariable Long userId) {
        return userService.getUserDto(userId);
    }

    @PutMapping("/user/{userId}")
    public void update(@RequestBody RequestUserUpdateDto request,
                       @PathVariable Long userId) {
        userService.update(request, userId);
    }

    @DeleteMapping("/user/{userId}")
    public void delete(@PathVariable Long userId) {
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(UserNotFoundException.message);
    }
}
