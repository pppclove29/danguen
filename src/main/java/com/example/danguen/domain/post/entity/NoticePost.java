package com.example.danguen.domain.post.entity;

import lombok.Getter;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("N")
public class NoticePost extends Post {
    @Override
    public Kind getKind() {
        return Kind.NOTICE;
    }
}
