package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.image.entity.Image;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponsePostDto {

    private Long id;
    private String title;
    private String content;
    private final List<String> imageUrl = new ArrayList<>();
    private final List<ResponseCommentDto> comments = new ArrayList<>();
    private String category;

    private int views;
    private LocalDateTime writtenTime;

    private String writer;

    public static ResponsePostDto toResponse(Post post) {
        ResponsePostDto dto = new ResponsePostDto();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setViews(post.getViews());
        dto.setWriter(post.getWriter().getName());
        dto.setWrittenTime(post.getCreatedTime());

        post.getImages().stream()
                .map(Image::getUuid)
                .forEach(dto.getImageUrl()::add);

        return dto;
    }
}
