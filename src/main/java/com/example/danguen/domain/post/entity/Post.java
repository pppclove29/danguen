package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.Comment;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public abstract class Post extends BaseTimeEntity implements PostKind {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POST_ID", nullable = false)
    protected Long id;

    protected String title;
    protected String content;

    private int views;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public void addViewCount() {
        views++;
    }

    @Override
    public Kind getKind() {
        return null;
    }
}
