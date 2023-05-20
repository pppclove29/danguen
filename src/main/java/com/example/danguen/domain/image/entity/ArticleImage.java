package com.example.danguen.domain.image.entity;

import com.example.danguen.domain.post.entity.Article;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("A")
public class ArticleImage extends Image{
    @ManyToOne
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    @Builder
    public ArticleImage(String name, String url, Article article){
        this.name = name;
        this.url = url;
        this.article = article;

        article.addImage(this);
    }
}
