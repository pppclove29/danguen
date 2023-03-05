package com.example.danguen.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private")
    public void sendPrivateMsg(Message message) {
        System.out.println("메시지 수신 완료");
        simpMessagingTemplate.convertAndSend("/queue/private/" + message.getRoomId(), message.getContent());
    }
}
