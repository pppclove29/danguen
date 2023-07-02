package com.example.danguen.domain.post.dto.response;

import com.example.danguen.domain.post.entity.Post;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@RedisHash(value = "post")
public class ResponsePostSimpleDto {
    @Id
    private Long id;
    private String title;
    private String imageUrl;
    private String writer;

    private int likeCount;
    private int commentCount;
    private LocalDateTime writtenTime;

    public static ResponsePostSimpleDto toResponse(Post post) {
        ResponsePostSimpleDto dto = new ResponsePostSimpleDto();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setWriter(post.getWriter().getName());
        // todo 흠... dto.setLikeCount(post.getInterestingUsers().size());
        dto.setCommentCount(post.getComments().size());
        post.getFirstImageUuid().ifPresent(dto::setImageUrl);
        dto.setWrittenTime(post.getCreatedTime());
        return dto;
    }
}
