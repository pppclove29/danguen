package com.example.danguen.domain.model.article.dto.request;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.article.Article;
import lombok.Data;

@Data
public class RequestArticleSaveOrUpdateDto {

    private String title;
    private String content;
    private int price;
    private String picture;
    private String category;
    private Address dealHopeAddress;

    public Article toEntity() {
        Article article = new Article();
        article.updateArticle(this);

        return article;
    }
}
