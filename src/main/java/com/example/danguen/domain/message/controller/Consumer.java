package com.example.danguen.domain.message.controller;

import com.example.danguen.domain.message.dto.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class Consumer {
    @RabbitListener(queues = "queue")
    public void consume(Message message){
        log.info("{}", message);
    }
}