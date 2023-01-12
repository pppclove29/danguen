package com.example.danguen.service.api;

import com.example.danguen.domain.user.dto.RequestUserPageDto;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ModelAndView getInfo(@PathVariable Long userId){

        // if userId 가 없거나 잘못되면 redirect or error

        ModelAndView mav=  new ModelAndView("/userPage");

        RequestUserPageDto user = userService.getUserPage(userId);

        mav.addObject(user);

        return mav;
    }
}
