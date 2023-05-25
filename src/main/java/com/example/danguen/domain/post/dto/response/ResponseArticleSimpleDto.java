package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.entity.ArticlePost;
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

    public static ResponseArticleSimpleDto toResponse(ArticlePost articlePost) {
        ResponseArticleSimpleDto dto = new ResponseArticleSimpleDto();

        dto.setId(articlePost.getId());
        dto.setTitle(articlePost.getTitle());
        dto.setPrice(articlePost.getPrice());
        dto.setViews(articlePost.getViews());
        dto.setDealHopeAddress(articlePost.getDealHopeAddress());
        dto.setImageUrl(articlePost.getImages().get(0).getUuid());

        return dto;
    }
}
