package com.example.danguen.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

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

    @MessageMapping("/private")
    public void sendPrivateMsg(Message message) {
        System.out.println("메시지 수신 완료");
        simpMessagingTemplate.convertAndSend("/queue/private/" + message.getRoomId(), message.getContent());
    }
}
