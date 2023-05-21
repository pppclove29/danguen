package com.example.danguen.domain.post.entity;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("F")
public class FreePost extends Post{
}
