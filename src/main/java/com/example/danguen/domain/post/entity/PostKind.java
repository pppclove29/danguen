package com.example.danguen.domain.post.entity;

public interface PostKind {
    enum Kind {
        ARTICLE(),
        FREE(),
        NOTICE()
    }

    Kind getKind();
}
