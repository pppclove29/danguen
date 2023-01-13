package com.example.danguen.domain.user;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.BaseTimeEntity;
import com.example.danguen.domain.article.Article;
import com.example.danguen.domain.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.user.dto.request.RequestSellerReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "USERS")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    private String name;
    private String email;
    private String picture;

    @Embedded
    private Address address;
    @Embedded
    private UserRate rate;

    @Enumerated(EnumType.STRING)
    private Role role;

    //@ManyToMany // 수정 필요 -> OneToMany and ManyToOne
    //List<User> interestUser;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    List<Article> sellArticles; // 판매상품

    //@OneToMany(cascade = CascadeType.ALL) -> OneToMany일까 ManyToMany일까?
    //List<Article> interestArticles; // 관심상품

    @Builder
    private User(String name, String email, String picture, Address address) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.address = address;

        rate = new UserRate();

        role = Role.ROLE_USER;
    }


    public void updateUser(RequestUserUpdateDto request) {
//현 유저에 대한 정보를 변경한다
        this.address= request.getAddress();
        this.name = request.getName();
    }

    public User updateOAuth(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public void reviewUser(RequestSellerReviewDto request) {
//현 유저에 대한 평가를 추가한다

    }

    public void addSellArticle(Article article){
        sellArticles.add(article);

        article.setSeller(this);
    }
    public void removeSellArticle(Article article){
        sellArticles.remove(article);
    }
}
