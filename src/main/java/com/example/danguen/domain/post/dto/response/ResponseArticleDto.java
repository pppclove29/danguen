package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.image.entity.Image;
import com.example.danguen.domain.post.entity.ArticlePost;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
public class ResponseArticleDto {

    private Long id;
    private String title;
    private String content;
    private int price;
    private List<String> imageUrl = new ArrayList<>();
    private List<ResponseCommentDto> comments = new ArrayList<>();
    private String category;

    private int views;
    private boolean isSold;
    private LocalDateTime writtenTime;

    private Address dealHopeAddress; // 거래 희망 장소

    private String seller; // 판매자

    public static ResponseArticleDto toResponse(ArticlePost articlePost) {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setId(articlePost.getId());
        dto.setTitle(articlePost.getTitle());
        dto.setContent(articlePost.getContent());
        dto.setPrice(articlePost.getPrice());
        dto.setCategory(articlePost.getCategory());
        dto.setViews(articlePost.getViews());
        dto.setSold(articlePost.isSold());
        dto.setDealHopeAddress(articlePost.getDealHopeAddress());
        dto.setSeller(articlePost.getWriter().getName());
        dto.setWrittenTime(articlePost.getCreatedTime());

        articlePost.getImages().stream()
                .map(Image::getUuid)
                .forEach(dto.getImageUrl()::add);

        return dto;
    }

    public void addComments(List<ResponseCommentDto> commentDtos) {
        setComments(commentDtos);
    }

}
