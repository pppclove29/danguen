package com.example.danguen.domain.model.comment.dto.request;

import com.example.danguen.domain.model.comment.Comment;
import lombok.Data;

@Data
public class RequestCommentSaveDto {
    String content;

    public Comment toEntity(){
        Comment comment = Comment.
    }
}
