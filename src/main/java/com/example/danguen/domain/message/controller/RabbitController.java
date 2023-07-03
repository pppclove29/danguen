package com.example.danguen.domain.message.controller;

import com.example.danguen.domain.message.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitController {
    private final Publisher rabbitPublisher;

    @GetMapping("/send")
    public void sendMessage() {
        Message rabbitMessage = new Message("sender", "1", "content");

        rabbitPublisher.sendMessage(rabbitMessage);
    }
}
