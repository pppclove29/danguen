package com.example.danguen.domain.comment.dto.response;

import com.example.danguen.domain.comment.entity.Comment;
import lombok.Data;

@Data
public class ResponseCommentDto {
    private Long id;
    private String writer;
    private String content;

    public static ResponseCommentDto toResponse(Comment comment) {
        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(comment.getId());
        dto.setWriter(comment.getWriter().getName());
        dto.setContent(comment.getContent());

        return dto;
    }
}
