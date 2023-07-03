package com.example.danguen.domain.message.controller;

import com.example.danguen.domain.message.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitMessageOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Publisher {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    public void sendMessage(Message message) {
        rabbitTemplate.convertAndSend(topicExchange.getName(), "hello.key.1", message);
    }
}