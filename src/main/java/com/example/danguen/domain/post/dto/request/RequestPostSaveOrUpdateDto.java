package com.example.danguen.domain.post.dto.request;

import com.example.danguen.domain.post.entity.FreePost;
import com.example.danguen.domain.post.entity.NoticePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestPostSaveOrUpdateDto {
    private final String title;
    private final String content;

    public Post toEntity(PostKind.Kind kind) {
        Post post;
        switch (kind) {
            case NOTICE -> post = new NoticePost();
            case FREE -> post = new FreePost();
            default -> throw new RuntimeException();
        }

        post.update(this);

        return post;
    }
}
