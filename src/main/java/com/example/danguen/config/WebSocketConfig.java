package com.example.danguen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // endpoint 연결용
                .setAllowedOriginPatterns("*") // cors
                .withSockJS();

        //var sock = new SockJS("/ws"); 프론트에서 다음과 같이 연결, 이걸 어떻게 백에서 테스트 못하나?
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue","topic"); // 메시지 큐 나누는 용도?
        registry.setApplicationDestinationPrefixes("/app");
    }
}
