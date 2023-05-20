package com.example.danguen.domain.image.dto;

import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.Article;
import com.example.danguen.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ImageDto {
    String name; // fileName + .jpg
    String path; // savePath + name

    public ImageDto(String name, String path) {
        this.name = name;
        this.path = path + name;
    }

    public ArticleImage toArticleImage(Article article) {
        return ArticleImage.builder()
                .name(this.name)
                .url(this.path)
                .article(article)
                .build();
    }

    public UserImage toUserImage(User user) {
        return UserImage.builder()
                .name(this.name)
                .url(this.path)
                .user(user)
                .build();
    }
}
