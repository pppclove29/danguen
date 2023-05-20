package com.example.danguen.domain.comment.dto.response;

import lombok.Data;

@Data
public class ResponseCommentDto {
    private Long id;
    private String writer;
    private String content;
}
