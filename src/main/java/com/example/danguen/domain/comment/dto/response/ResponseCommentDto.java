package com.example.danguen.domain.comment.dto.response;

import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseCommentDto {
    private Long id;
    private String writer;
    private String content;
    private LocalDateTime writtenTime;
    private int likeCount;

    public static ResponseCommentDto toResponse(Comment comment) {
        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(comment.getId());
        dto.setWriter(comment.getWriter().stream().map(User::getName).findAny().orElse("회원탈퇴 유저"));
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikedUser().size());
        dto.setWrittenTime(comment.getCreatedTime());
        return dto;
    }
}
