package com.example.danguen.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FormController {
    // 임시

    @GetMapping("/")
    public String index(){
        return "home";
    }
}
