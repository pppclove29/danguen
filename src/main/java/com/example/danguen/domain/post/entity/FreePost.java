package com.example.danguen.domain.post.entity;

import lombok.Getter;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("F")
public class FreePost extends Post{
    @Override
    public Kind getKind() {
        return Kind.FREE;
    }
}
