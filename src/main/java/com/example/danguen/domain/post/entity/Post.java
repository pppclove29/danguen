package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.base.BaseTimeEntity;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.post.dto.request.RequestPostSaveOrUpdateDto;
import com.example.danguen.domain.user.entity.User;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User writer;

    private int views;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void setWriter(User user) {
        writer = user;
    }

    public void addViewCount() {
        views++;
    }

    public void addImage(PostImage image) {
        images.add(image);
    }

    public Optional<String> getFirstImageUuid() {
        return images.isEmpty() ? Optional.empty() : Optional.of(images.get(0).getUuid());
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public void update(RequestPostSaveOrUpdateDto request){
        this.title = request.getTitle();
        this.content = request.getContent();
    }
}
