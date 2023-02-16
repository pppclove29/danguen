package com.example.danguen.config.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSessionHandlerAdapter extends StompSessionHandlerAdapter {
    @Value("${websocket.url.connect}")
    private String connectUrl;

    private Map<String, StompSession> sessions = new HashMap<>();
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        sessions.put(session.getSessionId(), session);

        session.send(connectUrl, session.getSessionId() + "님이 연결되었습니다");
    }
    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
        System.out.println("메세지 받앗습니다 여기는 CustomSessionHandlerAdapter 입니다");
    }

    public int getSessionsCount(){
        return sessions.size();
    }

    @Description("세션 초기화 테스트 외 사용 금지")
    public void sessionClear(){
        sessions.clear();
    }
}
