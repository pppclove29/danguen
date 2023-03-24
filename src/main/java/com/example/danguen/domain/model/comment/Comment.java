package com.example.danguen.domain.model.comment;

import com.example.danguen.domain.BaseTimeEntity;
import com.example.danguen.domain.model.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.model.user.User;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany
    @JoinColumn(name = "COMMENT_ID")
    private List<User> likedUser = new ArrayList<>(); // 좋아요을 누른 유저를 저장한다.

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
