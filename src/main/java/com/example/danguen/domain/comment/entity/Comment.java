package com.example.danguen.domain.comment.entity;

import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID", nullable = false)
    private Long id;

    private String content;
    private boolean isDeleted = false;

    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User writer;

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

        //todo 불안
        parentComment = null;

        writer.addComment(this);
        post.addComment(this);
    }


    public void updateComment(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }

    private void setParentComment(Comment parent) {
        this.parentComment = parent;
    }

    public void setChildComment(Comment child) {
        this.childrenComment.add(child);
        child.setParentComment(this);
    }

    public void deleteChildComment(Comment child) {
        this.childrenComment.remove(child);
    }

    public Optional<User> getWriter() {
        return Optional.ofNullable(this.writer);
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
