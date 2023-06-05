package com.example.danguen.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

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
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.MESSAGE,
                SimpMessageType.CONNECT_ACK,
                SimpMessageType.SUBSCRIBE,
                SimpMessageType.HEARTBEAT,
                SimpMessageType.DISCONNECT,
                SimpMessageType.DISCONNECT_ACK,
                SimpMessageType.OTHER,
                SimpMessageType.UNSUBSCRIBE).permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
