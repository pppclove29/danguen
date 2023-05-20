package com.example.danguen.domain.message.service;

import com.example.danguen.handler.CustomStompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {
    @Value("${websocket.url.connect}")
    private String connect_url;
    private final CustomStompHandler stompHandler;

    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.connect(connect_url, stompHandler);
    }
}
