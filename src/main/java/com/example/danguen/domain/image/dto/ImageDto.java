package com.example.danguen.domain.image.dto;

import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ImageDto {
    String path; // savePath

    public ImageDto(String path) {
        this.path = path;
    }

    public ArticleImage toArticleImage(ArticlePost articlePost) {
        return ArticleImage.builder()
                .url(this.path)
                .articlePost(articlePost)
                .build();
    }

    public UserImage toUserImage(User user) {
        return UserImage.builder()
                .url(this.path)
                .user(user)
                .build();
    }
}
