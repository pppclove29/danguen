package com.example.danguen.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-ep") // endpoint 연결용
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // topic 1:N 공지
        // queue 1:1 개인
        registry.enableSimpleBroker("/queue", "/topic");// 메시지 큐 나누는 용도?
        registry.setApplicationDestinationPrefixes("/pub");

        /* todo
            브로커 릴레이 적용
            브로커 릴레이를 통한 RabbitMQ 연결 및 BrokerAvailabilityEvent 연결 체크
            사용하려면 네티가 필요
        */
    }
}
