package com.example.danguen.domain.message.controller;

import com.example.danguen.handler.CustomStompHandler;
import com.example.danguen.domain.message.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CustomStompHandler stompHandler;

    //todo 직접 전파 없이 자동으로 브로드캐스트 해주는지 확인
    @MessageMapping("/private")
    public void sendPrivateMsg(Message message) {
        System.out.println("메시지 수신 완료");
        simpMessagingTemplate.convertAndSend("/queue/private/" + message.getRoomId(), message.getContent());
    }

    @GetMapping("/message/new")
    public void makeChat(@RequestParam Long targetUID){ // 새로운 채팅을 만들고자 할때 해당 메소드를 이용한다
        System.out.println("새로운 채팅 생성 시도");
        // 새로운 채팅을 할때면 송신자 또한 공통 room에 구독을 해야한다.
        // 이미 구독 중이라면 해당 과정은 넘어간다

        StompSession session = stompHandler.getStompSession();

        session.subscribe("/queue/private/" + targetUID, stompHandler);
    }
}
