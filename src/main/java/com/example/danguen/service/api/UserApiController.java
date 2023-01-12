package com.example.danguen.service.api;

import com.example.danguen.domain.user.dto.request.RequestUserJoinDto;
import com.example.danguen.domain.user.dto.request.RequestUserReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ModelAndView getInfo(@PathVariable Long userId) {
        // if userId 가 없거나 잘못되면 redirect or error

        ModelAndView mav = new ModelAndView("/userPage");

        ResponseUserPageDto user = userService.getUserPage(userId);

        mav.addObject(user);

        return mav;
    }

    @PutMapping("/user/{userId}")
    public String update(RequestUserUpdateDto request,
                         @PathVariable Long userId) {
        // if userId 가 없거나 잘못되면 redirect or error

        userService.update(request, userId);

        return "redirect:/userPage";
    }

    @PostMapping("/user/{userId}/review")
    public String review(RequestUserReviewDto request,
                         @PathVariable Long userId){
        // if userId 가 없거나 잘못되면 redirect or error

        userService.review(request, userId);

        return "userPage";
    }
}
