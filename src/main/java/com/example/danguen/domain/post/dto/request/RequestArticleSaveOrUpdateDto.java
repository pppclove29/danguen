package com.example.danguen.domain.post.dto.request;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.entity.ArticlePost;
import lombok.Builder;
import lombok.Data;

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

    public ArticlePost toEntity() {
        ArticlePost articlePost = new ArticlePost();
        articlePost.updateArticle(this);

        return articlePost;
    }
}
