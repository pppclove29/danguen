package com.example.danguen.domain.user.entity;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    private String name;
    private String email;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private final List<ArticlePost> sellArticlePosts = new ArrayList<>(); // 판매상품

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private final List<Post> writtenPosts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private final List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RATE_ID")
    private UserRate rate;

    @ManyToMany
    @JoinTable(
            name = "USER_INTEREST",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "INTEREST_USER_ID")
    )
    private final List<User> interestUsers = new ArrayList<>();

    @ManyToMany(mappedBy = "interestingUsers", cascade = CascadeType.ALL)
    private final List<ArticlePost> interestArticles = new ArrayList<>();

    @Builder
    private User(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;

        role = Role.USER;
    }

    public void updateUser(RequestUserUpdateDto request) {
        this.address = request.getAddress();
        this.name = request.getName();
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void review(RequestReviewDto request) {
        rate.applyReview(request);
    }

    public void addSellArticle(ArticlePost articlePost) {
        sellArticlePosts.add(articlePost);

        articlePost.setWriter(this);
    }

    public void removeSellArticle(ArticlePost articlePost) {
        sellArticlePosts.remove(articlePost);
    }

    public void addPost(Post post) {
        writtenPosts.add(post);
        post.setWriter(this);
    }

    public void removePost(Post post) {
        writtenPosts.remove(post);
    }

    public boolean isInterestUser(User iUser) {
        return interestUsers.contains(iUser);
    }

    public void addInterestUser(User iUser) {
        interestUsers.add(iUser);
    }

    public void removeInterestUser(User iUser) {
        interestUsers.remove(iUser);
    }

    public void addInterestArticle(ArticlePost articlePost) {
        interestArticles.add(articlePost);
    }

    public void removeInterestArticle(ArticlePost articlePost) {
        interestArticles.remove(articlePost);
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
