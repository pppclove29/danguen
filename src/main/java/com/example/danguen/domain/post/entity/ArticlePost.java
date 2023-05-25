package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@DiscriminatorValue("A")
public class ArticlePost extends Post {

    private int price;
    private String category;


    private boolean isSold;

    private Address dealHopeAddress; // 거래 희망 장소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User seller; // 판매자

    //@ManyToMany(mappedBy = "")
    //List<User> interests;


    @OneToMany(mappedBy = "articlePost", cascade = CascadeType.ALL)
    private List<ArticleImage> images = new ArrayList<>();

    public ArticlePost() {
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


    public void addInterest() {

    }

    public void removeInterest() {

    }


    public void addImage(ArticleImage image) {
        images.add(image);
    }
}
