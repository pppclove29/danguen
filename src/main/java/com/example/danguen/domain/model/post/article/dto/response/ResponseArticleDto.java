package com.example.danguen.domain.model.post.article.dto.response;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Data;


@Data
public class ResponseArticleDto {

    private String title;
    private String content;
    private int price;
    private String picture;
    private String category;

    private int views;
    private boolean isSold;

    private Address dealHopeAddress; // 거래 희망 장소

    private String seller; // 판매자

    public static ResponseArticleDto toResponse(Article article) {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setPrice(article.getPrice());
        dto.setPicture(article.getPicture());
        dto.setCategory(article.getCategory());
        dto.setViews(article.getViews());
        dto.setSold(article.isSold());
        dto.setDealHopeAddress(article.getDealHopeAddress());
        dto.setSeller(article.getSeller().getName());

        return dto;
    }
}