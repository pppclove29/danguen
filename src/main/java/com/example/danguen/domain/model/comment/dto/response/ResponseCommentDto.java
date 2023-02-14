package com.example.danguen.domain.model.comment.dto.response;

import lombok.Data;

@Data
public class ResponseCommentDto {
    private Long id;
    private String writer;
    private String content;
}
