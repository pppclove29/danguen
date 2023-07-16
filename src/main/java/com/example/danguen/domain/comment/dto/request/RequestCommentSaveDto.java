package com.example.danguen.domain.comment.dto.request;

import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.user.entity.User;
import lombok.Data;

@Data
public class RequestCommentSaveDto {
    private PostKind.Kind kind;
    private String content;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .writer(user)
                .post(post)
                .content(content)
                .build();
    }
}
