package com.example.danguen.domain.model.comment;

import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("A")
public class ArticleComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    @Builder
    public ArticleComment(User writer, Article article, String content) {
        this.writer = writer;
        this.article = article;
        this.content = content;

        article.addComment(this);
    }
}
