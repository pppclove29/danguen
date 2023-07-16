package com.example.danguen.domain.post.dto.request;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.entity.ArticlePost;
import lombok.Data;

@Data
public class RequestArticleSaveOrUpdateDto {

    private final RequestPostSaveOrUpdateDto postDto;
    private final int price;
    private final String category;
    private final Address dealHopeAddress;

    public ArticlePost toEntity() {
        ArticlePost articlePost = new ArticlePost();
        articlePost.updateArticle(this);

        return articlePost;
    }
}
