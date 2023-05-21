package com.example.danguen.domain.user.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    /*
    <Post Auth>
    save
        - admin
            - accept all
        - user
            - non notice post
        - anonymous
            - deny all
    update
        - admin
            - every notice post
            - own post
        - user
            - own post
        - anonymous
            - deny all
    delete
        - admin
            - accept all
        - user
            - own post
        - anonymous
            - deny all
    view
        - admin
            - accept all
        - user
            - accept all
        - anonymous
            - accept all
     */
}
