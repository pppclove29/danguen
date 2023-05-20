package com.example.danguen.domain.comment.dto.request;

import com.example.danguen.domain.comment.entity.ArticleComment;
import com.example.danguen.domain.post.entity.Article;
import com.example.danguen.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestCommentSaveDto {
    private String content;

    public ArticleComment toArticleComment(User user, Article article) {
        return ArticleComment.builder()
                .writer(user)
                .article(article)
                .content(content)
                .build();
    }
}
