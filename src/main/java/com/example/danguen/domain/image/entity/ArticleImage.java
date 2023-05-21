package com.example.danguen.domain.image.entity;

import com.example.danguen.domain.post.entity.ArticlePost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("A")
public class ArticleImage extends Image {
    @ManyToOne
    @JoinColumn(name = "ARTICLE_ID")
    private ArticlePost articlePost;

    @Builder
    public ArticleImage(String url, ArticlePost articlePost) {
        this.url = url;
        this.articlePost = articlePost;

        articlePost.addImage(this);
    }
}
