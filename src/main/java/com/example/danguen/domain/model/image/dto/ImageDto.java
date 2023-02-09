package com.example.danguen.domain.model.image.dto;

import com.example.danguen.domain.model.image.ArticleImage;
import com.example.danguen.domain.model.image.Image;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ImageDto {
    String name;
    String url;

    public static ImageDto toDto(MultipartFile image) {
        ImageDto dto = new ImageDto();

        dto.name = image.getName();

        return dto;
    }

    public ImageDto setUrl(String str) {
        url = str;

        return this;
    }

    public ArticleImage toArticleImage(Article article) {
        return ArticleImage.builder()
                .name(this.name)
                .url(this.url)
                .article(article)
                .build();
    }
}
