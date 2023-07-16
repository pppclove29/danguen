package com.example.danguen.domain.image.entity;

import com.example.danguen.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("U")
public class UserImage extends Image {
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public UserImage(String uuid, User user) {
        this.uuid = uuid;
        this.user = user;

        user.setImage(this);
    }
}
