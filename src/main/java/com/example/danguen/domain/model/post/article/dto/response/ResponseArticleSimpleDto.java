package com.example.danguen.domain.model.post.article.dto.response;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Data;

@Data
public class ResponseArticleSimpleDto {
    private Long id;
    private String title;
    private String imageUrl;

    private int price;
    private int views;
    private int likes;

    private Address dealHopeAddress; // 거래 희망 장소

    public static ResponseArticleSimpleDto toResponse(Article article) {
        ResponseArticleSimpleDto dto = new ResponseArticleSimpleDto();

        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setPrice(article.getPrice());
        dto.setViews(article.getViews());
        dto.setDealHopeAddress(article.getDealHopeAddress());

        return dto;
    }
}
