package com.example.danguen.domain.comment.entity;

import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID", nullable = false)
    Long id;

    String content;
    boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @OneToMany
    @JoinColumn(name = "COMMENT_ID")
    private final List<User> likedUser = new ArrayList<>(); // 좋아요을 누른 유저를 저장한다.

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private final List<Comment> childrenComment = new ArrayList<>();

    @Builder
    public Comment(User writer, Post post, String content) {
        this.writer = writer;
        this.post = post;
        this.content = content;

        writer.addComment(this);
        post.addComment(this);
    }


    public void updateComment(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void deleteUser() {
        this.writer = null;
    }

    public void likesComment(User user) {
        if (likedUser.contains(user))
            likedUser.remove(user);
        else
            likedUser.add(user);
    }
}
