package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.entity.ArticlePost;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseArticleSimpleDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String seller;

    private int price;
    private int likeCount;
    private int chatCount;
    private int commentCount;
    private Address dealHopeAddress; // 거래 희망 장소
    private LocalDateTime writtenTime;

    public static ResponseArticleSimpleDto toResponse(ArticlePost articlePost) {
        ResponseArticleSimpleDto dto = new ResponseArticleSimpleDto();

        dto.setId(articlePost.getId());
        dto.setTitle(articlePost.getTitle());
        dto.setPrice(articlePost.getPrice());
        dto.setSeller(articlePost.getSeller().getName());
        dto.setLikeCount(articlePost.getInterestingUsers().size());
        //todo chat 횟수 적용
        dto.setChatCount(0);
        dto.setCommentCount(articlePost.getComments().size());
        dto.setDealHopeAddress(articlePost.getDealHopeAddress());
        dto.setImageUrl(articlePost.getImages().get(0).getUuid());
        dto.setWrittenTime(articlePost.getCreatedTime());
        return dto;
    }
}
