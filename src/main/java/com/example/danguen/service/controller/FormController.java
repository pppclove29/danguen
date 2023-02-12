package com.example.danguen.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FormController {

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");

        return mav;
    }

    @GetMapping("/article-list")
    public ModelAndView articleList() {
        ModelAndView mav = new ModelAndView("articleList");

        return mav;
    }

    @GetMapping("/article-page")
    public ModelAndView articlePage() {
        ModelAndView mav = new ModelAndView("articlePage");

        return mav;
    }

    @GetMapping("/user-page")
    public ModelAndView userPage() {
        ModelAndView mav = new ModelAndView("userPage");

        return mav;
    }
}
