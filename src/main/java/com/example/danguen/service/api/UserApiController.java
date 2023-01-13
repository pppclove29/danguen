package com.example.danguen.service.api;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.user.dto.request.RequestSellerReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ModelAndView getInfo(@PathVariable Long userId) {
        ModelAndView mav = new ModelAndView("/userPage");

        ResponseUserPageDto user = userService.getUserPage(userId);

        mav.addObject(user);

        return mav;
    }

    @PutMapping("/user/{userId}")
    public String update(RequestUserUpdateDto request,
                         @PathVariable Long userId) {
        userService.update(request, userId);

        return "redirect:/userPage";
    }

    @PostMapping("/user/{userId}/review")
    public String review(RequestSellerReviewDto request,
                         @PathVariable Long userId){
        userService.review(request, userId);

        return "userPage";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(){
        return "userNotFound";
    }
}
