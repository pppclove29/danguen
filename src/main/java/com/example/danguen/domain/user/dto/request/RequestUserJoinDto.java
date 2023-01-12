package com.example.danguen.domain.user.dto.request;

import com.example.danguen.domain.user.User;
import lombok.Data;

@Data
public class RequestUserJoinDto {

    public User toEntity(){
        User user = new User();

        return user;
    }
}
