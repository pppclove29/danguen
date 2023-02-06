package com.example.danguen.domain.model.post.article.dto.response;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Data;

@Data
public class ResponseArticleSimpleDto {
    private String title;
    private String picture;

    private int price;
    private int views;
    private int likes;

    private Address dealHopeAddress; // 거래 희망 장소

    public static ResponseArticleSimpleDto toResponse(Article article) {
        ResponseArticleSimpleDto dto = new ResponseArticleSimpleDto();

        dto.setTitle(article.getTitle());
        dto.setPrice(article.getPrice());
        dto.setPicture(article.getPicture());
        dto.setViews(article.getViews());
        dto.setDealHopeAddress(article.getDealHopeAddress());

        return dto;
    }
}
