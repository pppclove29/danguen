package com.example.danguen.domain.article;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.BaseTimeEntity;
import com.example.danguen.domain.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.article.dto.response.ResponseArticleDto;
import com.example.danguen.domain.user.User;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Article extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ARTICLE_ID", nullable = false)
    private Long id;

    private String title;
    private String content;
    private int price;
    private String picture;
    private String category;

    private int views;
    private boolean isSold;

    private Address dealHopeAddress; // 거래 희망 장소

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User seller; // 판매자

    //@ManyToMany(mappedBy = "")
    //List<User> interests;

    //List<Comment> comments;

    public Article() {
        isSold = false;
    }

    public void updateArticle(RequestArticleSaveOrUpdateDto request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.price = request.getPrice();
        this.picture = request.getPicture();
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

    public void addComment() {

    }

    public void removeComment() {

    }

    public ResponseArticleDto toResponse() {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setTitle(title);
        dto.setContent(content);
        dto.setPrice(price);
        dto.setPicture(picture);
        dto.setCategory(category);
        dto.setViews(views);
        dto.setSold(isSold);
        dto.setDealHopeAddress(dealHopeAddress);
        dto.setSeller(seller.getName());

        return dto;
    }
}
