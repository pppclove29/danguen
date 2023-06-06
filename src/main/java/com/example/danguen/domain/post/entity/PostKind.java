package com.example.danguen.domain.post.entity;

import com.example.danguen.domain.post.repository.ArticlePostRepository;

public interface PostKind {
    enum Kind{
        ARTICLE(),
        FREE()
    }

    Kind getKind();
}
