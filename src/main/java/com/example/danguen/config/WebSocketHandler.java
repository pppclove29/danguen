package com.example.danguen.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println("세션연결" + session);
//        System.out.println(session.getId());
//        sessionMap.put(session.getId(), session);
//
//        MessageDto msg = new MessageDto();
//        msg.setSender(session.getId());
//        msg.setContent("누군가가 들어왔단다");
//
//        sessionMap.values().forEach(receiver -> {
//            if (!receiver.getId().equals(session.getId())) {
//                try {
//                    receiver.sendMessage(new TextMessage(msg.getContent()));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("메세지 전송" + session + " " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("세션종료" + session);
    }

}
