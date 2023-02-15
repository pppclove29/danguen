package com.example.danguen.service.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/connect")
    @SendTo("/topic")
    public String getMessage(@Payload String message) {
        System.out.println(message);
        return message;
    }

    @MessageMapping("/private-message/{sessionId}")
    @SendTo("/user/{sessionId}/queue/private")
    public String sendPrivateMsg(@Payload String message,
                                 @DestinationVariable String sessionId) {
        System.out.println(sessionId);
        System.out.println(message);
        return message;
    }
}
