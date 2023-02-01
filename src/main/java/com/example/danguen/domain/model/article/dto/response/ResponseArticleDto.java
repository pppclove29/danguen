package com.example.danguen.domain.model.article.dto.response;

import com.example.danguen.domain.Address;
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


}
