package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.post.entity.Article;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    private Address dealHopeAddress; // 거래 희망 장소

    private String seller; // 판매자

    public static ResponseArticleDto toResponse(Article article) {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setPrice(article.getPrice());
        dto.setCategory(article.getCategory());
        dto.setViews(article.getViews());
        dto.setSold(article.isSold());
        dto.setDealHopeAddress(article.getDealHopeAddress());
        dto.setSeller(article.getSeller().getName());

        article.getImages().stream().map((image) -> dto.getImageUrl().add(image.getUrl()));

        return dto;
    }

    public void addComments(Stream<ResponseCommentDto> commentDtoStream) {
        setComments(commentDtoStream.collect(Collectors.toList()));
    }

}
