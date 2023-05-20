package com.example.danguen.domain.message.dto;

import lombok.Data;

@Data
public class Message {

    private String sender;
    private String roomId;
    private String content;
}
