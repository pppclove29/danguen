package com.example.danguen.ui;

import com.example.danguen.argumentResolver.LoginUserName;
import com.example.danguen.domain.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FormController {
    // 임시

    @GetMapping("/")
    public String index(Model model,
                        @LoginUserName String userName) {
        model.addAttribute("userName",userName);

        return "home";
    }
}
