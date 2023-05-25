package com.example.danguen.domain.user.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
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
    @Column(unique = true)
    private String email;

    @Embedded
    private Address address;
    @Embedded
    private UserRate rate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "USER_INTEREST",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "INTEREST_USER_ID")
    )
    private final List<User> interestUsers = new ArrayList<>();

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

        role = Role.USER;
    }

    public void updateUser(RequestUserUpdateDto request) {
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
        return interestUsers.contains(iUser);
    }

    public void addInterestUser(User iUser) {
        interestUsers.add(iUser);
    }

    public void deleteInterestUser(User iUser) {
        interestUsers.remove(iUser);
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

    public void setImage(UserImage userImage) {
        this.image = userImage;
    }
}
