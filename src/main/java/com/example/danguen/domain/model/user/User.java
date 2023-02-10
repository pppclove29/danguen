package com.example.danguen.domain.model.user;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.BaseTimeEntity;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.model.image.UserImage;
import com.example.danguen.domain.model.post.article.Article;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "USERS") // H2때문에 선언
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    private String name;
    private String email;

    @Embedded
    private Address address;
    @Embedded
    private UserRate rate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany
    private List<User> interestUser = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Article> sellArticles = new ArrayList<>(); // 판매상품

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserImage image;

    //@OneToMany(cascade = CascadeType.ALL) -> OneToMany일까 ManyToMany일까?
    //List<Article> interestArticles; // 관심상품

    @Builder
    private User(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;

        rate = new UserRate();

        role = Role.ROLE_USER;
    }


    public void updateUser(RequestUserUpdateDto request) {
        //현 유저에 대한 정보를 변경한다
        this.address = request.getAddress();
        this.name = request.getName();
    }

    public User updateOAuth(String name, String email) {
        this.name = name;
        this.email = email;

        return this;
    }

    public void reviewSeller(RequestSellerReviewDto request) {
        //현 유저가 판매자였을때의 평가를 추가한다
        rate.applyReview(request);
    }

    public void reviewBuyer(RequestBuyerReviewDto request) {
        //현 유저가 구매자였을때의 평가를 추가한다
        rate.applyReview(request);
    }

    public void addSellArticle(Article article) {
        sellArticles.add(article);

        article.setSeller(this);
    }

    public void removeSellArticle(Article article) {
        sellArticles.remove(article);
    }

    public void addInterestUser(User iUser) {
        interestUser.add(iUser);
    }

    public void deleteInterestUser(User iUser) {
        interestUser.remove(iUser);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public void giveUpComments() {
        for (Comment comment : comments) {
            comment.deleteUser();
        }
    }

    public UserImage setImage(UserImage userImage) {
        this.image = userImage;

        return userImage;
    }
}
