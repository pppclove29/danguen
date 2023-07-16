package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import lombok.Getter;

import jakarta.persistence.*;
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

    @ManyToMany
    @JoinTable(
            name = "ARTICLE_INTEREST",
            joinColumns = @JoinColumn(name = "ARTICLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    private List<User> interestingUsers = new ArrayList<>();

    public ArticlePost() {
        isSold = false;
    }

    public void updateArticle(RequestArticleSaveOrUpdateDto request) {
        super.update(request.getPostDto());
        this.price = request.getPrice();
        this.category = request.getCategory();
        this.dealHopeAddress = request.getDealHopeAddress();
    }

    public void sold() {
        isSold = true;
    }

    public void addInterest(User user) {
        interestingUsers.add(user);
        user.addInterestArticle(this);
    }

    public void removeInterest(User user) {
        interestingUsers.remove(user);
        user.removeInterestArticle(this);
    }

    @Override
    public Kind getKind() {
        return Kind.ARTICLE;
    }
}
