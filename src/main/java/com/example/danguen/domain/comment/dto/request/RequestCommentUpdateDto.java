package com.example.danguen.domain.comment.dto.request;

import com.example.danguen.domain.post.entity.PostKind;
import lombok.Data;

@Data
public class RequestCommentUpdateDto {
    private PostKind.Kind kind;
    private String comment;
}
