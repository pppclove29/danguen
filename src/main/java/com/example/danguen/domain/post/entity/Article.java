package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.ArticleComment;
import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Article extends BaseTimeEntity implements Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ARTICLE_ID", nullable = false)
    private Long id;

    private String title;
    private String content;
    private int price;
    private String category;

    private int views;
    private boolean isSold;

    private Address dealHopeAddress; // 거래 희망 장소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User seller; // 판매자

    //@ManyToMany(mappedBy = "")
    //List<User> interests;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ArticleComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ArticleImage> images = new ArrayList<>();

    public Article() {
        isSold = false;
    }

    public void updateArticle(RequestArticleSaveOrUpdateDto request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.price = request.getPrice();
        this.category = request.getCategory();
        this.dealHopeAddress = request.getDealHopeAddress();
    }

    public void setSeller(User user) {
        seller = user;
    }

    public void sold() {
        isSold = true;
    }

    public void addViewCount() {
        views++;
    }

    public void addInterest() {

    }

    public void removeInterest() {

    }

    public void addComment(ArticleComment articleComment) {
        comments.add(articleComment);
    }

    public void removeComment(ArticleComment articleComment) {
        comments.remove(articleComment);
    }

    public ArticleImage addImage(ArticleImage image) {
        images.add(image);

        return image;
    }
}
