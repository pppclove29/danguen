package com.example.danguen.domain.user;

import com.example.danguen.domain.article.Article;
import com.example.danguen.domain.user.dto.request.RequestUserReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "USERS")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    private String name;
    private String email;
    private String address;
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    private float dealTemperature;
    private float reDealHopePercent;
    private float responseRate;
    //List<String> rate;

    //@ManyToMany // 수정 필요 -> OneToMany and ManyToOne
    //List<User> interestUser;

    //@OneToMany
    //List<Article>  articles;

    @Builder
    private User(String name, String email, String address, String picture) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.picture = picture;

        role = Role.ROLE_USER;
    }


    public void updateUser(RequestUserUpdateDto request) {
//현 유저에 대한 정보를 변경한다

    }
    public User updateOAuth(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public void reviewUser(RequestUserReviewDto request) {
//현 유저에 대한 평가를 추가한다

    }
}
