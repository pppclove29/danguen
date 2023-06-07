package com.example.danguen.domain.post.entity;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("N")
public class NoticePost extends Post {
    @Override
    public Kind getKind() {
        return Kind.NOTICE;
    }
}
