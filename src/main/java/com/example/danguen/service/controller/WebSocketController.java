package com.example.danguen.service.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @MessageMapping("/ws-endpoint")
    @SendTo("/topic/messages")
    public String end(String message) throws Exception {
        return "test";
    }

    @MessageMapping("/connect")
    @SendTo("/topic/message")
    public String connect(@Payload String message) {
        System.out.println(message + " controller의 connect에서 처리합니다");
        return message;
    }

    @MessageMapping("/topic")
    @SendTo("/topic/message")
    public String topic(@Payload String message) {
        System.out.println(message + " controller의 topic에서 처리합니다");
        return message;
    }

    @MessageMapping("/private-message/{sessionId}")
    @SendToUser("/queue/private")
    public String sendPrivateMsg(@Payload String message,
                                 @DestinationVariable String sessionId) {
        System.out.println(message + " controller의 sendPrivateMsg에서 처리합니다");
        return message;
    }
}
