package com.example.danguen.domain.image.entity;

import lombok.Getter;

import jakarta.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "IMAGE_ID")
    Long id;

    String uuid;
}
