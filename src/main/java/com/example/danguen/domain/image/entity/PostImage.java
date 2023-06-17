package com.example.danguen.domain.image.entity;

import com.example.danguen.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("A")
public class PostImage extends Image {
    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Builder
    public PostImage(String uuid, Post post) {
        this.uuid = uuid;
        this.post = post;

        post.addImage(this);
    }
}
