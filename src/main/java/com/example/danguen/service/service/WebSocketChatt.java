package com.example.danguen.service.service;

import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@ServerEndpoint(value = "/chatt")
public class WebSocketChatt {
    private static Set<Session> clients =
            Collections.synchronizedSet(new HashSet<>());

    @OnMessage
    public void onMessage(String msg, Session session) throws Exception {
        System.out.println("메세지 전송 :" + msg);

        clients.forEach(s -> {
            if (!s.getId().equals(session.getId())) {
                try {
                    s.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @OnOpen
    public void onOpen(Session session) throws Exception {
        System.out.println("세션연결" + session);
        System.out.println(session.getId());
        clients.add(session);

        String msg = "누군가 들어왔음";

        onMessage(msg, session);
    }

    @OnClose
    public void onClose(Session s) {

    }
}
