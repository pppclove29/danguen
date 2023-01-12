package com.example.danguen.domain.user;

import com.example.danguen.domain.article.Article;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    String nickname;
    String address;

    float dealTemperature;
    float reDealHopePercent;
    float responseRate;
    //List<String> rate;

    @ManyToMany // 수정 필요 -> OneToMany and ManyToOne
    List<User> interestUser;

    @OneToMany
    List<Article>  articles;
}
