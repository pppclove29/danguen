package com.example.danguen.domain.image.dto;

import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ImageDto {
    String uuid;

    public ImageDto(String uuid) {
        this.uuid = uuid;
    }

    public PostImage toPostImage(Post post) {
        return PostImage.builder()
                .uuid(this.uuid)
                .post(post)
                .build();
    }

    public UserImage toUserImage(User user) {
        return UserImage.builder()
                .uuid(this.uuid)
                .user(user)
                .build();
    }
}
