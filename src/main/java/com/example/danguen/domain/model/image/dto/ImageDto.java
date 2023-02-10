package com.example.danguen.domain.model.image.dto;

import com.example.danguen.domain.model.image.ArticleImage;
import com.example.danguen.domain.model.image.Image;
import com.example.danguen.domain.model.image.UserImage;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


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
