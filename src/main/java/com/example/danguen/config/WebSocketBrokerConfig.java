package com.example.danguen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-endpoint") // endpoint 연결용
                .setAllowedOriginPatterns("*")
                .withSockJS(); // cors
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // topic 1:N 공지
        // queue 1:1 개인
        registry.enableSimpleBroker("/queue", "topic"); // 메시지 큐 나누는 용도?
        registry.setApplicationDestinationPrefixes("/appDes");
    }



}
