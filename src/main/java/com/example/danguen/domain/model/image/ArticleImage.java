package com.example.danguen.domain.model.image;

import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
