package com.example.danguen.domain.model.post.article.dto.request;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RequestArticleSaveOrUpdateDto {

    private final String title;
    private final String content;
    private final int price;
    private final String category;
    private final Address dealHopeAddress;

    @Builder
    public RequestArticleSaveOrUpdateDto(String title, String content, int price, String category, Address dealHopeAddress) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.category = category;
        this.dealHopeAddress = dealHopeAddress;
    }

    public Article toEntity() {
        Article article = new Article();
        article.updateArticle(this);

        return article;
    }
}
