package com.example.danguen.domain.model.comment.dto.request;

import com.example.danguen.domain.model.comment.ArticleComment;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import lombok.Data;

@Data
public class RequestCommentSaveDto {
    String content;

    public ArticleComment toArticleComment(User user, Article article) {
        return ArticleComment.builder()
                .writer(user)
                .article(article)
                .content(content)
                .build();
    }
}
