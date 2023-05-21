package com.example.danguen.domain.user.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.review.RequestReviewDto;
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
//TODO 각종 컬럼에 대해 Nullable and unique 설정
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
    private final List<User> interestUser = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private final List<ArticlePost> sellArticlePosts = new ArrayList<>(); // 판매상품

    @OneToMany(mappedBy = "writer")
    private final List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
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

    public void review(RequestReviewDto request) {
        rate.applyReview(request);
    }

    public void addSellArticle(ArticlePost articlePost) {
        sellArticlePosts.add(articlePost);

        articlePost.setSeller(this);
    }

    public void removeSellArticle(ArticlePost articlePost) {
        sellArticlePosts.remove(articlePost);
    }

    public boolean isInterestUser(User iUser) {
        return interestUser.contains(iUser);
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
        // 작성한 모든 댓글에 대한 소유권을 포기한다
        for (Comment comment : comments) {
            comment.deleteUser();
        }
    }

    public UserImage setImage(UserImage userImage) {
        this.image = userImage;

        return userImage;
    }
}
